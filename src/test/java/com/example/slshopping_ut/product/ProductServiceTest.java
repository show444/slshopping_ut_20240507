package com.example.slshopping_ut.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.example.slshopping_ut.entity.Brand;
import com.example.slshopping_ut.entity.Category;
import com.example.slshopping_ut.entity.Product;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    /** モック化したクラス */
    @Mock
    private ProductRepository mockProductRepository;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private ProductService target;

    /**
     * 【概要】
     * 商品のリストを取得<br>
     *
     * 【条件】
     * productRepositoryのfindAllメソッドはProductのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Productのリストを返却すること
     */
    @Test
    void testListAll() {
        List<Product> expected = Arrays.asList(
            new Product(1L, "productA", "descriptionA", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(1L, "categoryA"), new Brand(1L, "brandA")),
            new Product(2L, "productB", "descriptionB", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(2L, "categoryB"), new Brand(2L, "brandB"))
        );

        //スタブの設定
        doReturn(expected).when(this.mockProductRepository).findAll();

        //検証処理
        assertThat(target.listAll()).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 商品を検索<br>
     *
     * 【条件】
     * productServiceのlistAllメソッドにnullを渡すこと<br>
     * productRepositoryのfindAllメソッドはProductのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Productのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNull() {
        List<Product> expected = Arrays.asList(
            new Product(1L, "productA", "descriptionA", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(1L, "categoryA"), new Brand(1L, "brandA")),
            new Product(2L, "productB", "descriptionB", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(2L, "categoryB"), new Brand(2L, "brandB"))
        );

        //スタブの設定
        doReturn(expected).when(this.mockProductRepository).findAll();

        //検証処理
        assertThat(target.listAll(null)).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 商品を検索<br>
     *
     * 【条件】
     * productServiceのlistAllメソッドに空文字を渡すこと<br>
     * productRepositoryのfindAllメソッドはProductのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Productのリストを返却すること
     */
    @Test
    void testListAll_argumentIsEmpty() {
        List<Product> expected = Arrays.asList(
            new Product(1L, "productA", "descriptionA", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(1L, "categoryA"), new Brand(1L, "brandA")),
            new Product(2L, "productB", "descriptionB", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(2L, "categoryB"), new Brand(2L, "brandB"))
        );

        //スタブの設定
        doReturn(expected).when(this.mockProductRepository).findAll();

        //検証処理
        assertThat(target.listAll("")).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 商品を検索<br>
     *
     * 【条件】
     * productServiceのlistAllメソッドにproductという文字列を渡すこと<br>
     * productRepositoryのsearchメソッドはProductのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Productのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNotEmpty() {
        String keyword = "product";

        List<Product> expected = Arrays.asList(
            new Product(1L, "productA", "descriptionA", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(1L, "categoryA"), new Brand(1L, "brandA")),
            new Product(2L, "productB", "descriptionB", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(2L, "categoryB"), new Brand(2L, "brandB"))
        );

        doReturn(expected).when(this.mockProductRepository).search(keyword);

        assertThat(target.listAll(keyword)).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 商品名の重複チェック<br>
     *
     * 【条件】
     * productRepositoryのfindByNameメソッドはnullを返すようスタブ化すること<br>
     *
     * 【結果】
     * trueを返すこと
     */
    @Test
    void testCheckUnique_noDuplication() {
        //商品名が重複していない商品情報を作成
        Product newProduct = new Product(1L, "productA", "descriptionA", 1, null, 1.0, 1.0, 1.0, 1.0, new Category(1L, "categoryA"), new Brand(1L, "brandA"));

        //スタブの設定
        doReturn(null).when(this.mockProductRepository).findByName(anyString());

        //検証処理
        assertThat(target.checkUnique(newProduct)).isTrue();
    }

    /**
     * 【概要】
     * 商品名の重複チェック<br>
     *
     * 【条件】
     * productRepositoryのfindByNameメソッドはProductのインスタンスを返却するようスタブ化すること<br>
     *
     * 【結果】
     * falseを返すこと
     */
    @Test
    void testCheckUnique_duplicate() {
        //商品名が重複する商品情報を作成
        Product newProduct = new Product();
        newProduct.setName("product");

        //スタブに設定するデータを作成
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("product");

        //スタブの設定
        doReturn(mockProduct).when(this.mockProductRepository).findByName(newProduct.getName());

        //検証処理
        assertThat(target.checkUnique(newProduct)).isFalse();
    }

    /**
     * 【概要】
     * 商品情報の取得<br>
     *
     * 【条件】
     * productRepositoryのfindByIdメソッドはProductのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生しないこと
     */
    @Test
    void testGet_noThrowsException() {
        //準備 テストデータに存在するID
        Long id = 1L;

        //スタブに設定するデータを作成
        Optional<Product> product = Optional.of(new Product());

        //スタブの設定
        doReturn(product).when(this.mockProductRepository).findById(id);

        //検証処理
        assertThatCode(() -> {
            target.get(id);
        }).doesNotThrowAnyException();
    }

    /**
     * 【概要】
     * 商品情報の取得<br>
     *
     * 【条件】
     * productRepositoryのfindByIdメソッドはnullを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生すること
     */
    @Test
    void testGet_throwsException() {
        //準備 テストデータに存在しないID
        Long id = 1000L;

        //スタブに設定するデータを作成
        Optional<Product> product = Optional.ofNullable(null);

        //スタブの設定
        doReturn(product).when(this.mockProductRepository).findById(id);

        //検証処理
        assertThatThrownBy(() -> {
            target.get(id);
        })
        .isInstanceOf(NotFoundException.class);
    }

    /**
     * 【概要】
     * 商品情報の取得処理の検証<br>
     *
     * 【条件】
     * productRepositoryのfindByIdはProductのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Productを返却すること
     */
    @Test
    void testGet() throws Exception {
        //準備 テストデータに存在するID
        Long id = 1L;

        //スタブに設定するデータを作成
        Optional<Product> product = Optional.of(new Product());

        //スタブの設定
        doReturn(product).when(this.mockProductRepository).findById(id);

        //検証処理
        assertThat(target.get(id)).isEqualTo(product.get());
    }
}
