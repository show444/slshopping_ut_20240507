package com.example.slshopping_ut.product;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.slshopping_ut.brand.BrandService;
import com.example.slshopping_ut.category.CategoryService;
import com.example.slshopping_ut.entity.Brand;
import com.example.slshopping_ut.entity.Category;
import com.example.slshopping_ut.entity.Product;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    /** モック化したクラス */
    @Mock
    private ProductService mockProductService;

    @Mock
    private BrandService mockBrandService;

    @Mock
    private CategoryService mockCategoryService;

    @Mock
    private ProductImageService mockProductImageService;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private ProductController target;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        //MockMvcの生成
        this.mockMvc = MockMvcBuilders.standaloneSetup(target).alwaysDo(log()).build();
    }

    /**
     * 【概要】
     * 商品一覧表示画面の検証<br>
     *
     * 【条件】
     * GET通信の/productsにリクエストすること<br>
     * クエリパラメーターkeywordにはnullを入力すること<br>
     * productServiceのfindAllメソッドは商品のリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * ステータスが200であること<br>
     * products/products.htmlを表示すること<br>
     * キー名listProductsに商品のリストが格納されていること<br>
     * キー名keywordにnullが格納されていること
     */
    @Test
    void testListProducts() throws Exception {
        //準備
        List<Product> products = new ArrayList<>();
        String keyword = null;

        //スタブを設定
        //doReturn(返り値の設定).when(対象のモック).対象のメソッド（引数）
        doReturn(products).when(this.mockProductService).listAll(keyword);

        //検証
        /*
         * perform(get("path") httpメソッドとパスの指定
         * param("key", val) クエリストリングの指定
         * andExpect(検証したいこと)
         * 検証例：
         * status().isOk() ステータスコードの検証
         * view().name("テンプレートファイル名") テンプレートファイルの呼び出しがあっているか
         * model().attribute("key", val) modelに格納されているか
         */
        this.mockMvc.perform(get("/products").param("keyword", keyword)) // リクエストの情報
                .andExpect(status().isOk()) // ステータスの検証
                .andExpect(view().name("products/products")) // テンプレートファイルの呼び出し検証
                .andExpect(model().attribute("listProducts", products)) // modelに格納されている要素の検証
                .andExpect(model().attribute("keyword", keyword));

    }

    /**
     * 【概要】
     * 商品新規登録画面の検証<br>
     *
     * 【条件】
     * GET通信の/products/newにリクエストすること<br>
     *
     * 【結果】
     * ステータスが200であること<br>
     * products/product_form.htmlを表示すること<br>
     * キー名productにProductのインスタンスが格納されていること
     */
    @Test
    void testNewProduct() throws Exception {
        //検証
        /*
         * 検証例：
         * model().attribute("key", instanceOf(対象クラス名.class)) モデルに格納されているクラスが、対象インスタンスか
         */
        this.mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_form"))
                .andExpect(model().attribute("product", instanceOf(Product.class)));

    }

    /**
     * 【概要】
     * 商品新規登録処理の検証
     *
     * 【条件】
     * POST通信の/products/saveにリクエストすること<br>
     * バリデーションを通過する値をパラメーターにすること<br>
     * productImageServiceのisValidメソッドはtrueを返却するようスタブ化すること<br>
     * productServiceのcheckUniqueメソッドはtrueを返却するようスタブ化すること<br>
     * productsServiceのsaveメソッドはProductのインスタンスを返却するようスタブ化すること<br>
     *
     * 【結果】
     * ステータスが302であること<br>
     * /productsにリダイレクトしていること<br>
     * リダイレクト先にキー名success_messageに「登録に成功しました」という文字列が格納されていること
     */
    @Test
    void testSaveProduct() throws Exception {
        //準備
        Brand brand = new Brand(1L, "brandA");
        Category category = new Category(1L, "categoryA");
        Product product = new Product(1L, "productA", "description", 1, "image",
            1.0, 1.0, 1.0, 1.0, category, brand);

        //スタブを設定
        doReturn(true).when(this.mockProductImageService).isValid(null);

        doReturn(true).when(this.mockProductService).checkUnique(product);
        
        doReturn(product).when(this.mockProductService).save(product);

        //検証
        /*
         * 検証例：
         * redirectedUrl("path") リダイレクト先の検証
         * flash().attribute("key", val) // リダイレクト時の引継ぎ情報の検証
         */
        this.mockMvc.perform(post("/products/save")
                .flashAttr("product", product))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success_message", "登録に成功しました"));

    }

    /**
     * 【概要】
     * 商品詳細画面の検証<br>
     *
     * 【条件】
     * GET通信の/products/detail/1にリクエストすること<br>
     * productServiceのgetメソッドはID1LのProductを返却するようスタブ化すること<br>
     *
     * 【結果】
     * ステータスが200であること<br>
     * products/product_detail.htmlを表示すること<br>
     * キー名productにID1LのProductが格納されていること
     */
    @Test
    void testDetailProduct() throws Exception {
        //準備
        Long id = 1L;
        Product product = new Product();

        //スタブを設定
        doReturn(product).when(this.mockProductService).get(id);

        //検証
        this.mockMvc.perform(get("/products/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_detail"))
                .andExpect(model().attribute("product", product));
    }

    /**
     * 【概要】
     * 商品編集画面の検証<br>
     *
     * 【条件】
     * GET通信の/products/edit/1にリクエストすること<br>
     * productServiceのgetメソッドがID1LのProductを返却するようスタブ化すること<br>
     *
     * 【結果】
     * ステータスが200であること<br>
     * products/product_edit.htmlを表示すること<br>
     * キー名productにID1LのProductが格納されていること
     */
    @Test
    void testEditProductForm() throws Exception {
        //準備
        Long id = 1L;
        Product product = new Product();

        //スタブを設定
        when(this.mockProductService.get(id)).thenReturn(product);

        //検証
        this.mockMvc.perform(get("/products/edit/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product_edit"))
                .andExpect(model().attribute("product", product));

    }

    /**
     * 【概要】
     * 商品更新処理の検証
     *
     * 【条件】
     * POST通信の/products/edit/1にリクエストすること<br>
     * バリデーションを通過する値をパラメーターにすること<br>
     * productImageServiceのisValidメソッドはtrueを返却するようスタブ化すること<br>
     * productServiceのcheckUniqueメソッドはtrueを返却するようスタブ化すること<br>
     * productsServiceのsaveメソッドはProductのインスタンスを返却するようスタブ化すること<br>
     *
     * 【結果】
     * ステータスが302であること<br>
     * /productsにリダイレクトしていること<br>
     * リダイレクト先にキー名success_messageに「更新に成功しました」という文字列が格納されていること
     */
    @Test
    void testEditProduct() throws Exception {
        //準備
        Long id = 1L;
        Brand brand = new Brand(1L, "brandA");
        Category category = new Category(1L, "categoryA");
        Product product = new Product(id, "productA", "description", 1, "image",
            1.0, 1.0, 1.0, 1.0, category, brand);

        //スタブを設定
        doReturn(true).when(this.mockProductImageService).isValid(null);
        doReturn(true).when(this.mockProductService).checkUnique(product);
        doReturn(product).when(this.mockProductService).save(product);

        //検証
        this.mockMvc.perform(post("/products/edit/{id}", id).flashAttr("product", product))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success_message", "更新に成功しました"));

    }

    /**
     * 【概要】
     * 商品削除処理の検証<br>
     *
     * 【条件】
     * GET通信の/products/delete/1にリクエストすること<br>
     *
     * 【結果】
     * ステータスが302であること<br>
     * /productsにリダイレクトしていること<br>
     * リダイレクト先にキー名success_messageに「削除に成功しました」という文字列が格納されていること
     */
    @Test
    void testDeleteProduct() throws Exception {
        //準備
        Long id = 1L;

        //スタブの設定
        doNothing().when(this.mockProductService).delete(id);

        //検証
        this.mockMvc.perform(get("/products/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("success_message", "削除に成功しました"));

    }
}
