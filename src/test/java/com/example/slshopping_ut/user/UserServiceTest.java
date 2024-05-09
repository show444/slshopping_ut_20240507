package com.example.slshopping_ut.user;

import static org.assertj.core.api.Assertions.*;
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

import com.example.slshopping_ut.entity.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /** モック化したクラス */
    @Mock
    private UserRepository mockUserRepository;

    /** テスト対象クラスにモックを注入 */
    @InjectMocks
    private  UserService target;

    /**
     * 【概要】
     * 管理者を検索<br>
     *
     * 【条件】
     * userServiceのlistAllメソッドにnullを渡すこと<br>
     * userRepositoryのfindAllメソッドはUserのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Userのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNull() {
      List<User> expected = Arrays.asList(
        new User(1L, "aaa@example.com", "test1", "userA", false, null),
        new User(2L, "bbb@example.com", "test2", "userB", false, null)
      );

      //スタブの設定
      doReturn(expected).when(this.mockUserRepository).findAll();

      //検証
      assertThat(target.listAll(null)).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 管理者を検索<br>
     *
     * 【条件】
     * userServiceのlistAllメソッドに空文字を渡すこと<br>
     * userRepositoryのfindAllメソッドはUserのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Userのリストを返却すること
     */
    @Test
    void testListAll_argumentIsEmpty() {
      List<User> expected = Arrays.asList(
        new User(1L, "aaa@example.com", "test1", "userA", false, null),
        new User(2L, "bbb@example.com", "test2", "userB", false, null)
      );

      //スタブの設定
      doReturn(expected).when(this.mockUserRepository).findAll();

      //検証
      assertThat(target.listAll("")).isEqualTo(expected);
    }

    /**
     * 【概要】
     * 管理者を検索<br>
     *
     * 【条件】
     * userServiceのlistAllメソッドにuserという文字列を渡すこと<br>
     * userRepositoryのsearchメソッドはUserのリストを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Userのリストを返却すること
     */
    @Test
    void testListAll_argumentIsNotEmpty() {
      String keyword = "brand";

      List<User> expected = Arrays.asList(
        new User(1L, "aaa@example.com", "test1", "userA", false, null),
        new User(2L, "bbb@example.com", "test2", "userB", false, null)
      );

      //スタブの設定
      doReturn(expected).when(this.mockUserRepository).search(keyword);

      //検証
      assertThat(target.listAll(keyword)).isEqualTo(expected);

    }

   /**
     * 【概要】
     * 管理メールアドレスの重複チェック<br>
     *
     * 【条件】
     * userRepositoryのfindByEmailメソッドはnullを返すようスタブ化すること<br>
     *
     * 【結果】
     * trueを返すこと
     */
    @Test
    void testCheckUnique_noDuplication() {
      //メールアドレスが重複していないユーザー情報を作成
      User newUser = new User(1L, "aaa@example.com", "test1", "userA", false, null);

      //スタブの設定
      doReturn(null).when(this.mockUserRepository).findByEmail(newUser.getEmail());

      //検証
      assertThat(target.checkUnique(newUser)).isTrue();
    }

    /**
     * 【概要】
     * 管理メールアドレスの重複チェック<br>
     *
     * 【条件】
     * userRepositoryのfindByEmailメソッドはUserのインスタンスを返すようスタブ化すること<br>
     *
     * 【結果】
     * falseを返すこと
     */
    @Test
    void testCheckUnique_duplicate() {
      //メールアドレスが重複するユーザー情報を作成
      User newUser = new User();
      newUser.setEmail("aaa@example.com");

      //スタブに設定するデータを作成
      User mockUser = new User();
      newUser.setEmail("aaa@example.com");

      //スタブの設定
      doReturn(mockUser).when(this.mockUserRepository).findByEmail(newUser.getEmail());

      //検証
      assertThat(target.checkUnique(newUser)).isFalse();

    }

    /**
     * 【概要】
     * 管理者情報の取得<br>
     *
     * 【条件】
     * userRepositoryのfindByIdメソッドはUserのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生しないこと
     */
    @Test
    void testGet_noThrowsException() {
      //準備 テストデータに存在するID
      Long id = 1L;

      //スタブに設定するデータを作成
      Optional<User> user = Optional.of(new User());

      //スタブの設定
      doReturn(user).when(this.mockUserRepository).findById(id);

      //検証
      assertThatCode(() -> {
        target.get(id);
      }).doesNotThrowAnyException();
    }

    /**
     * 【概要】
     * 管理者情報の取得<br>
     *
     * 【条件】
     * userRepositoryのfindByIdメソッドはnullを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * 例外が発生すること
     */
    @Test
    void testGet_throwsException() {
      //準備 テストデータに存在しないID
      Long id = 1000L;

      //スタブに設定するデータを作成
      Optional<User> user = Optional.ofNullable(null);

      //スタブの設定
      doReturn(user).when(this.mockUserRepository).findById(id);

      //検証
      assertThatThrownBy(() -> {
        target.get(id);
      })
      .isInstanceOf(NotFoundException.class);
    }

    /**
     * 【概要】
     * 管理者情報の取得処理の検証<br>
     *
     * 【条件】
     * userRepositoryのfindByIdはUserのインスタンスを格納したOptionalを返却するようスタブ化すること<br>
     *
     * 【結果】
     * Userを返却すること
     */
    @Test
    void testGet() throws Exception {
      //準備 テストデータに存在するID
      Long id = 1L;

      //スタブに設定するデータを作成
      Optional<User> user = Optional.of(new User());

      //スタブの設定
      doReturn(user).when(this.mockUserRepository).findById(id);

      //検証
      assertThat(target.get(id)).isEqualTo(user.get());
    }
}
