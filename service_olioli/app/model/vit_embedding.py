import torch
import torch.nn as nn
import torch.nn.functional as F
from transformers import ViTModel
from torchvision import transforms
from PIL import Image

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


class ViTEmbeddingModel(nn.Module):
    def __init__(self, vit, embedding_dim=128):
        super(ViTEmbeddingModel, self).__init__()
        self.vit = vit
        self.embedding = nn.Linear(vit.config.hidden_size, embedding_dim)

    def forward(self, x):
        outputs = self.vit(pixel_values=x)
        pooled_output = outputs.pooler_output  # [batch_size, hidden_size]
        return self.embedding(pooled_output)

def get_vit_model(model_name: str):
    """
    Tải pretrained ViT từ transformers (ví dụ: 'google/vit-base-patch16-224-in21k')
    """
    vit = ViTModel.from_pretrained(model_name)
    return vit


def load_model(model_path, model_name, embedding_dim):
    # Load kiến trúc
    vit = get_vit_model(model_name)
    model = ViTEmbeddingModel(vit, embedding_dim)

    # Load trọng số đã train
    model.load_state_dict(torch.load(model_path, map_location=device))

    # Đưa model lên GPU (nếu có)
    model.to(device)
    model.eval()

    print(f"Model loaded successfully on {device}")
    if torch.cuda.is_available():
        print(f"Using device: {torch.cuda.get_device_name(0)}")
    return model


def get_embedding(model, image_path):
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.5, 0.5, 0.5], std=[0.5, 0.5, 0.5])
    ])

    image = Image.open(image_path).convert("RGB")
    image = transform(image).unsqueeze(0).to(device)

    with torch.no_grad():
        tensor_emb = model(image)
    return tensor_emb.cpu().numpy() 
