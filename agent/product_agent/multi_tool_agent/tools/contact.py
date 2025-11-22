
import os
from google.adk.tools.function_tool import FunctionTool


class ContactTool:
    """
    Tool để cung cấp thông tin liên hệ của cửa hàng.
    """

    @staticmethod
    def get_contact_info() -> str:
        """
        Trả về thông tin liên hệ của cửa hàng.
        """
        hotline = os.getenv('HOTLINE', '1900 636 099')
        email = os.getenv('EMAIL', 'olioli_shop@gmail.com')
        return f"Bạn có thể liên hệ với cửa hàng Olioli qua hotline: {hotline} hoặc email: {email} để được hỗ trợ thêm."
    

contact_tool = ContactTool()

suggest_contact_info_tool = FunctionTool(contact_tool.get_contact_info)