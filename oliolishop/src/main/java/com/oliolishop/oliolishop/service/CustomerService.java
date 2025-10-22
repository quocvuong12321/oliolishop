package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.address.AddressRequest;
import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.address.AddressUpdateRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AddressMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.mapper.LocationMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.AddressRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import com.oliolishop.oliolishop.repository.WardRepository;
import com.oliolishop.oliolishop.util.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    CustomerMapper customerMapper;
    AccountRepository accountRepository;

    AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final LocationMapper locationMapper;
    private final WardRepository wardRepository;


    public CustomerResponse createCustomer(CustomerRequest request, String accountId) {
        Account c = accountRepository.findById(accountId).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Customer customer = customerMapper.toCustomer(request);

        String id = UUID.randomUUID().toString();
        customer.setAccount(c);
        customer.setId(id);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    public List<AddressResponse> getAddresses() {
        String customerId = AppUtils.getCustomerIdByJwt();

        List<Address> addresses = addressRepository.findByCustomerIdWithDetail(customerId).orElse(new ArrayList<>());

        return addresses.stream().map(address -> {
            AddressResponse response = addressMapper.toResponse(address);
            response.setDefaultAddress(address.isDefaultAddress());
            response.setWard(locationMapper.toWardDTO(address.getWard()));
            response.setProvince(locationMapper.toProvinceDTO(address.getWard().getDistrict().getProvince()));
            response.setDistrict(locationMapper.toDistrictDTO(address.getWard().getDistrict()));
            return response;


        }).toList();
    }

    public AddressResponse createAddress(AddressRequest request) {

        String customerId = AppUtils.getCustomerIdByJwt();

        boolean existAddress = addressRepository.existsByCustomerId(customerId);

        if(!existAddress)
            request.setIsDefault(true);

        if(request.getIsDefault()) {
            Address address = addressRepository.findByCustomerIdAndDefaultAddress(customerId,true).orElse(null);
            if(address!=null){
                address.setDefaultAddress(false);
                addressRepository.save(address);
            }
        }

        Ward ward = wardRepository.findByIdWithDetails(request.getWardId()).orElseThrow(()->new AppException(ErrorCode.ADDRESS_NOT_EXIST));
        Address newAddress = addressMapper.toAddress(request);

        newAddress.setCustomer(Customer.builder()
                .id(customerId)
                .build()); //Chỉ cần ấy id thôi vì đây là reference ảo nên khi lưu nó chỉ lấy customer id là đủ
        newAddress.setDefaultAddress(request.getIsDefault());
        newAddress.setWard(ward);
        newAddress.setId(UUID.randomUUID().toString());

        AddressResponse response = addressMapper.toResponse(addressRepository.save(newAddress));
        response.setDefaultAddress(newAddress.isDefaultAddress());
        response.setWard(locationMapper.toWardDTO(ward));
        response.setDistrict(locationMapper.toDistrictDTO(ward.getDistrict()));
        response.setProvince(locationMapper.toProvinceDTO(ward.getDistrict().getProvince()));

        return response;
    }

    public AddressResponse updateAddress(AddressUpdateRequest request, String addressId) {

        String customerId = AppUtils.getCustomerIdByJwt();

        Address address = addressRepository.findByIdAndCustomer_Id(addressId,customerId).orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        if(request.getIsDefault()){
            Address addressDefault = addressRepository.findByCustomerIdAndDefaultAddress(customerId,true).orElse(null);
            if(addressDefault!=null){
                addressDefault.setDefaultAddress(false);
                addressRepository.save(addressDefault);
            }
        }

        Ward ward = wardRepository.findByIdWithDetails(request.getWardId()).orElseThrow(()->new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        address.setDetailAddress(request.getDetailAddress());
        address.setDefaultAddress(request.getIsDefault());
        address.setName(request.getName());
        address.setWard(ward);
        address.setPhoneNumber(request.getPhoneNumber());

        AddressResponse response = addressMapper.toResponse(addressRepository.save(address));
        response.setDefaultAddress(request.getIsDefault());
        response.setWard(locationMapper.toWardDTO(ward));
        response.setDistrict(locationMapper.toDistrictDTO(ward.getDistrict()));
        response.setProvince(locationMapper.toProvinceDTO(ward.getDistrict().getProvince()));
        return response;
    }

    public void deleteAddress(String addressId) {
        String customerId = AppUtils.getCustomerIdByJwt();

        Address address = addressRepository.findByIdAndCustomer_Id(addressId,customerId).orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        addressRepository.delete(address);
    }

    public CustomerResponse updateCustomer(CustomerRequest request, MultipartFile file, String imageDir, String folderName) throws IOException {
        String customerId = AppUtils.getCustomerIdByJwt();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        customer.setDob(request.getDob());

        customer.setGender(Customer.Gender.valueOf(request.getGender()));

        customer.setName(request.getName());

        String avatarUrl = saveAvatar(customerId, file, imageDir, folderName);

        customer.setImage(avatarUrl);

        return customerMapper.toResponse(customerRepository.save(customer));

    }


    // Hàm này nên nằm trong FileService hoặc ImageUtils
    private String saveAvatar(String customerId, MultipartFile file, String imageDir, String folderName) throws IOException {

        // 1. Tạo đường dẫn tuyệt đối để lưu
        Path uploadPath = Paths.get(imageDir, folderName);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Đảm bảo thư mục tồn tại
        }
        // 2. Định nghĩa tên file: uniqueId + timestamp + extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String newFileName = customerId + "-" + fileExtension;
        File outputFile = uploadPath.resolve(newFileName).toFile();

        // 3. Xử lý ảnh bằng Thumbnails: Cắt và Resize
        try {
            Thumbnails.of(file.getInputStream())
                    // Cắt xén (Crop): Cắt ảnh thành hình vuông từ tâm
                    .crop(net.coobird.thumbnailator.geometry.Positions.CENTER)
                    .size(500, 500)
                    .outputQuality(1)
                    .toFile(outputFile);
        } catch (IOException e) {
            // Log lỗi
            throw new IOException("Failed to process and save avatar file.", e);
        }

        // 4. Trả về đường dẫn để lưu vào Database
        return folderName + "/" + newFileName; // Trả về URL tương đối
    }
}
