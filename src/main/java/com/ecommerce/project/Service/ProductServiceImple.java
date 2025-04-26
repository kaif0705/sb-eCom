package com.ecommerce.project.Service;

import com.ecommerce.project.Model.Cart;
import com.ecommerce.project.Model.Category;
import com.ecommerce.project.Model.Product;
import com.ecommerce.project.Payload.CartDTO;
import com.ecommerce.project.Payload.ProductDTO;
import com.ecommerce.project.Payload.ProductResponse;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Service
public class ProductServiceImple implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    //Add Product
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId){

        Product product = modelMapper.map(productDTO, Product.class);

        Category category= categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Product productNameFromDB= productRepository.findByProductName(product.getProductName());

        if(productNameFromDB != null){
            //Product already exists
            throw new APIException("Product already exists with Product Name: " + product.getProductName());
        }else{
            product.setImage("Default.png");
            double specialPrice= product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setCategory(category);
        }


        Product savedProduct= productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    // Get All Products
    public ProductResponse getAllProduct(Integer pageNo, Integer pageSize, String sortBy, String sortOrder){

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productsPage= productRepository.findAll(pageDetails);
        List<Product> products= productsPage.getContent();

//        Pagination pagination= new Pagination();
//
//        List<Product> products= pagination.pagination(pageNo, pageSize, sortBy, sortOrder);

        //checking if no products exists, .isEmpty coz it is a List
        if(products.isEmpty()){
            throw new ResourceNotFoundException("No products found");
        }

        List<ProductDTO> productDTO= products.stream().
                map((product) -> modelMapper.map(product, ProductDTO.class)).
                toList();

        ProductResponse productResponse= new ProductResponse();

        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    //Get Products By Category
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder){

//        Pagination pagination;

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        
        Category category= categoryRepository.findById(categoryId).
                            orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        //findByCategoryOrderByPriceAsc is our custom method for finding category of the product in the product repository
//        List<Product> productByCategory= productRepository.findByCategoryOrderByPriceAsc(category);

        //Pagination
        Pageable pageDetails= PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productsPage= productRepository.findAllByCategory(category, pageDetails);
        List<Product> productByCategory= productsPage.getContent();

        //Check weather category is empty
        if(productByCategory.isEmpty()){
            throw new ResourceNotFoundException("Empty Category!!");
        }

        List<ProductDTO> productDTO= productByCategory.stream().
                map((product)-> modelMapper.map(product, ProductDTO.class)).
                toList();

        ProductResponse productResponse= new ProductResponse();

        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    //Get Products By Keyword
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortOrder){

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //findByProductNameLikeIgnoreCase is our custom method for finding product in the product repository with keyword passed
        // if car is passed, all products which contain car in them will be sent as response
        //% for matching pattern
        //Pagination
        Pageable pageDetails= PageRequest.of(pageNo, pageSize, sortByAndOrder);
        Page<Product> productsPage= productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> allProducts= productsPage.getContent();

        //Check weather product exists or not
        if(allProducts.isEmpty()){
            throw new ResourceNotFoundException("No such products found");
        }

        List<ProductDTO> productDTO= allProducts.stream().
                map((product) -> modelMapper.map(product, ProductDTO.class)).
                toList();

        ProductResponse productResponse= new ProductResponse();

        productResponse.setContent(productDTO);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }


    //Update products
    public ProductDTO updateProduct(Long productId, ProductDTO productDto){

        Product product= modelMapper.map(productDto, Product.class);

        Product products= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        products.setProductName(product.getProductName());
        products.setProductDescription(product.getProductDescription());
        products.setPrice(product.getPrice());
        products.setDiscount(product.getDiscount());
        products.setProductQuantity(product.getProductQuantity());

        Product savedProduct= productRepository.save(products);

        //After updating the product it should also reflect in each and every cart of the user
        List<Cart> carts= cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTO= carts.stream().map(
            cart -> {
                CartDTO cartDTOs= modelMapper.map(cart, CartDTO.class);

                List<ProductDTO> productDTOs= cart.getCartItem().stream().map(
                        cartItem -> {
                            return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                        }
                ).toList();
                cartDTOs.setProducts(productDTOs);

                return cartDTOs;
            }
        ).toList();

        cartDTO.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }


    //Delete Products
    public ProductDTO deleteProduct(Long productId){

        Product products= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Delete product from users cart as well
        List<Cart> carts= cartRepository.findCartsByProductId(productId);

        carts.forEach(
                cart -> {
                    cartService.deleteProductFromCart(cart.getCartId(), productId);
                }
        );

        productRepository.delete(products);
        return modelMapper.map(products, ProductDTO.class);

    }


    //Update Image
    public ProductDTO updateImage(Long productId, MultipartFile image){

        //Get productFromDB
        Product productFromDB= productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Upload image to server
        //Get the file name of uploaded image
        String fileName= fileService.uploadImage(path, image);

        //Updating the new file name to the product
        productFromDB.setImage(fileName);

        //Saving the product into the DB
        Product savedProduct= productRepository.save(productFromDB);

        //Return DTO after mapping
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

}
