package com.example.slshopping_ut.category;

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

import com.example.slshopping_ut.entity.Category;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    /** モック化したクラス */
    @Mock
    private CategoryRepository mockCategoryRepository;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private CategoryService target;

    /**
     * 【概要】
     * カテゴリーのリストを取得<br>
     *
     * 【条件】
     * categoryRepositoryのfindAllメソッドはCategoryのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Categoryのリストを返却すること
     */
    @Test
    void testListAll() {
        List<Category> expected = Arrays.asList(
            new Category(1L, "categoryA"),
            new Category(2L, "categoryB")
        );

        //スタブの設定
        doReturn(expected).when(this.mockCategoryRepository).findAll();

        //検証処理
        assertThat(target.listAll()).isEqualTo(expected);

    }

    /**
     * 【概要】
     * カテゴリーを検索<br>
     *
     * 【条件】
     * categoryServiceのlistAllメソッドにnullを渡すこと<br>
     * categoryRepositoryのfindAllメソッドはCategoryのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Categoryのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNull() {
        List<Category> expected = Arrays.asList(
            new Category(1L, "categoryA"),
            new Category(2L, "categoryB")
        );

        //スタブの設定
        doReturn(expected).when(this.mockCategoryRepository).findAll();

        //検証処理
        assertThat(target.listAll(null)).isEqualTo(expected);
    }

    /**
     * 【概要】
     * カテゴリーを検索<br>
     *
     * 【条件】
     * categoryServiceのlistAllメソッドに空文字を渡すこと<br>
     * categoryRepositoryのfindAllメソッドはCategoryのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Categoryのリストを返却すること
     */
    @Test
    void testListAll_argumentIsEmpty() {
        List<Category> expected = Arrays.asList(
            new Category(1L, "categoryA"),
            new Category(2L, "categoryB")
        );

        //スタブの設定
        doReturn(expected).when(this.mockCategoryRepository).findAll();

        //検証処理
        assertThat(target.listAll("")).isEqualTo(expected);
    }

    /**
     * 【概要】
     * カテゴリーを検索<br>
     *
     * 【条件】
     * categoryServiceのlistAllメソッドにcategoryという文字列を渡すこと<br>
     * categoryRepositoryのsearchメソッドはCategoryのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Categoryのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNotEmpty() {
        //listAllメソッドに渡す文字列
        String keyword = "category";

        List<Category> expected = Arrays.asList(
            new Category(1L, "categoryA"),
            new Category(2L, "categoryB")
        );

        //スタブの設定
        doReturn(expected).when(this.mockCategoryRepository).search(keyword);

        //検証処理
        assertThat(target.listAll(keyword)).isEqualTo(expected);
    }

    /**
     * 【概要】
     * カテゴリー名の重複チェック<br>
     *
     * 【条件】
     * categoryRepositoryのfindByNameメソッドはnullを返すようスタブ化すること<br>
     *
     * 【結果】
     * trueを返すこと
     */
    @Test
    void testCheckUnique_noDuplication() {
        //カテゴリ名が重複していないブランド情報を作成
        Category newCategory = new Category(1L, "categoryA");

        //スタブの設定
        doReturn(null).when(this.mockCategoryRepository).findByName(anyString());

        //検証処理
        assertThat(target.checkUnique(newCategory)).isTrue();
    }

    /**
     * 【概要】
     * カテゴリー名の重複チェック<br>
     *
     * 【条件】
     * categoryRepositoryのfindByNameメソッドはCategoryのインスタンスを返却するようスタブ化すること<br>
     *
     * 【結果】
     * falseを返すこと
     */
    @Test
    void testCheckUnique_duplicate() {
        //準備 カテゴリ名が重複するカテゴリ情報を作成
        Category newCategory = new Category(1L, "categoryA");

        //スタブに設定するデータを作成
        Category mockCategory = new Category(1L, "categoryA");

        //スタブの設定
        doReturn(mockCategory).when(this.mockCategoryRepository).findByName(newCategory.getName());

        //検証処理
        assertThat(target.checkUnique(newCategory)).isFalse();
    }

    /**
     * 【概要】
     * カテゴリー情報の取得<br>
     *
     * 【条件】
     * categoryRepositoryのfindByIdメソッドはCategoryのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生しないこと
     */
    @Test
    void testGet_noThrowsException() {
        //準備 テストデータに存在するID
        Long id = 1L;

        //スタブに設定するデータを作成
        Optional<Category> category = Optional.of(new Category());

        //スタブの設定
        doReturn(category).when(this.mockCategoryRepository).findById(id);

        //検証処理
        assertThatCode(() -> {
            target.get(id);
        }).doesNotThrowAnyException();
    }

    /**
     * 【概要】
     * カテゴリー情報の取得<br>
     *
     * 【条件】
     * categoryRepositoryのfindByIdメソッドはnullを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生すること
     */
    @Test
    void testGet_throwsException() {
        //準備 テストデータに存在しないID
        Long id = 1000L;

        //スタブに設定するデータを作成
        Optional<Category> category = Optional.ofNullable(null);

        //スタブの設定
        doReturn(category).when(this.mockCategoryRepository).findById(id);

        //検証処理
        assertThatThrownBy(() ->{
            target.get(id);
        })
        .isInstanceOf(NotFoundException.class);
    }

    /**
     * 【概要】
     * カテゴリー情報の取得処理の検証<br>
     *
     * 【条件】
     * categoryRepositoryのfindByIdはCategoryのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Categoryを返却すること
     */
    @Test
    void testGet() throws Exception {
        //準備 任意のID
        Long id = 1L;

        //スタブに設定するデータを作成
        Optional<Category> category = Optional.of(new Category());

        //スタブの設定
        doReturn(category).when(this.mockCategoryRepository).findById(id);

        //検証処理
        assertThat(target.get(id)).isEqualTo(category.get());
    }
}
