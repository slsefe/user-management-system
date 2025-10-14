package com.zixi.usermanagementsystem.service;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zixi.usermanagementsystem.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testSave() {
        User user = buildUserWithDefaultValue();
        userService.save(user);
        Assertions.assertNotNull(user.getId());
        System.out.println("user = " + user);
    }

    @Test
    void testSaveBatch() {
        List<User> users = new ArrayList<>();
        users.add(buildUserWithDefaultValue());
        users.add(buildUserWithDefaultValue());
        boolean success = userService.saveBatch(users);
        Assertions.assertTrue(success);
        System.out.println("users = " + users);
    }

    @Test
    void testSaveBatchWithBatchSize() {
        List<User> users = new ArrayList<>();
        users.add(buildUserWithDefaultValue());
        users.add(buildUserWithDefaultValue());
        boolean success = userService.saveBatch(users, 1);
        Assertions.assertTrue(success);
        System.out.println("users = " + users);
    }

    @Test
    void testSaveOrUpdate() {
        User user = buildUserWithDefaultValue();
        user.setId(1L);
        boolean success = userService.saveOrUpdate(user);
        Assertions.assertTrue(success);
        System.out.println("user = " + user);
    }

    @Test
    void testSaveOrUpdateBatch() {
        List<User> users = new ArrayList<>();
        User newUser = buildUserWithDefaultValue();
        users.add(newUser);
        User existedUser = buildUserWithDefaultValue();
        existedUser.setId(1L);
        users.add(existedUser);
        boolean success = userService.saveOrUpdateBatch(users);
        Assertions.assertTrue(success);
        System.out.println("users = " + users);
    }

    @Test
    void testGetOne() {
        Wrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getId, 1L);
        User user = userService.getOne(wrapper);
        System.out.println("user = " + user);
    }

    @Test
    void testGetById() {
        User user = userService.getById(1);
        System.out.println("user = " + user);
    }

    @Test
    void testCount() {
        long count = userService.count();
        System.out.println("count = " + count);
    }

    @Test
    void testList() {
        List<User> users = userService.list();
        System.out.println("users = " + users);
    }

    @Test
    void testUpdateById() {
        User user = buildUserWithDefaultValue();
        user.setId(1L);
        boolean success = userService.updateById(user);
        Assertions.assertTrue(success);
    }

    @Test
    void testUpdateWithWrapper() {
        User user = buildUserWithDefaultValue();
        user.setId(1L);
        boolean success = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, user.getId()));
        Assertions.assertTrue(success);
    }

    @Test
    void testUpdateBatchById() {
        List<User> users = new ArrayList<>();
        users.add(buildUserWithDefaultValue());
        users.add(buildUserWithDefaultValue());
        boolean success = userService.updateBatchById(users);
        Assertions.assertTrue(success);
    }

    @Test
    void testRemoveById() {
        boolean success = userService.removeById(1L);
        Assertions.assertTrue(success);
    }

    @Test
    void testRemoveByIds() {
        List<User> users = new ArrayList<>();
        users.add(buildUserWithDefaultValue());
        users.add(buildUserWithDefaultValue());
        boolean success = userService.removeByIds(users);
        Assertions.assertTrue(success);
    }

    @Test
    void testRemoveBatchByIds() {
        List<User> users = new ArrayList<>();
        users.add(buildUserWithDefaultValue());
        users.add(buildUserWithDefaultValue());
        boolean success = userService.removeBatchByIds(users);
        Assertions.assertTrue(success);
    }

    @Test
    void testPageWithoutCondition() {
        IPage<User> page = Page.of(1, 3);
        IPage<User> pagedResult = userService.page(page);
        pagedResult.getRecords().forEach(System.out::println);
    }



    private static User buildUserWithDefaultValue() {
        User user = new User();
        user.setUsername("slsefe");
        user.setAccount("20250101");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setPassword("password");
        user.setPhone("123");
        user.setEmail("456");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0);
        user.setDeleted(0);
        return user;
    }


}