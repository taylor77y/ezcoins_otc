package com.ezcoins.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.validation.Validator;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class CreateCollectionService {

    /** 设置集合名称 */
    private static final String COLLECTION_NAME = "users17";

    private static final String MESSAGE_COLLECTION_NAME = "message1";

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 创建【集合】
     *
     * 创建一个大小没有限制的集合（默认集合创建方式）
     *
     * @return 创建集合的结果
     */
    public Object createCollection() {
        // 设置集合名称
        String collectionName = "message1";
        // 创建集合并返回集合信息
        mongoTemplate.createCollection(collectionName);
        // 检测新的集合是否存在，返回创建结果
        return mongoTemplate.collectionExists(collectionName) ? "创建视图成功" : "创建视图失败";
    }

    /**
     * 插入【一条】文档数据，如果文档信息已经【存在就抛出异常】
     *
     * @return 插入的文档信息
     */
    public Object insertMessage(String message) {
        // 设置用户信息
        HelloMessage hello = new HelloMessage().setName(message);
        // 插入一条用户数据，如果文档信息已经存在就抛出异常
        HelloMessage newHelloMessage = mongoTemplate.insert(hello, MESSAGE_COLLECTION_NAME);
        // 输出存储结果
        log.info("存储的用户信息为：{}", newHelloMessage);
        return newHelloMessage;
    }

    /**
     * 插入【一条】文档数据，如果文档信息已经【存在就抛出异常】
     *
     * @return 插入的文档信息
     */
    public Object insert() {
        // 设置用户信息
        User user = new User()
//                .setId("10")
                .setAge(22)
                .setSex("男")
                .setRemake("无")
                .setSalary(1500)
                .setName("zhangsan")
                .setBirthday(new Date())
                .setStatus(new Status().setHeight(180).setWeight(150));
        // 插入一条用户数据，如果文档信息已经存在就抛出异常
        User newUser = mongoTemplate.insert(user, COLLECTION_NAME);
        // 输出存储结果
        log.info("存储的用户信息为：{}", newUser);
        return newUser;
    }

    /**
     * 创建【固定大小集合】
     *
     * 创建集合并设置 `capped=true` 创建 `固定大小集合`，可以配置参数 `size` 限制文档大小，可以配置参数 `max` 限制集合文档数量。
     *
     * @return 创建集合的结果
     */
    public Object createCollectionFixedSize() {
        // 设置集合名称
        String collectionName = "users20";
        // 设置集合参数
        long size = 1024L;
        long max = 5L;
        // 创建固定大小集合
        CollectionOptions collectionOptions = CollectionOptions.empty()
                // 创建固定集合。固定集合是指有着固定大小的集合，当达到最大值时，它会自动覆盖最早的文档。
                .capped()
                // 固定集合指定一个最大值，以千字节计(KB),如果 capped 为 true，也需要指定该字段。
                .size(size)
                // 指定固定集合中包含文档的最大数量。
                .maxDocuments(max);
        // 执行创建集合
        mongoTemplate.createCollection(collectionName, collectionOptions);
        // 检测新的集合是否存在，返回创建结果
        return mongoTemplate.collectionExists(collectionName) ? "创建视图成功" : "创建视图失败";
    }

    /**
     * 创建【验证文档数据】的集合
     *
     * 创建集合并在文档"插入"与"更新"时进行数据效验，如果符合创建集合设置的条件就进允许更新与插入，否则则按照设置的设置的策略进行处理。
     *
     * * 效验级别：
     *   - off：关闭数据校验。
     *   - strict：(默认值) 对所有的文档"插入"与"更新"操作有效。
     *   - moderate：仅对"插入"和满足校验规则的"文档"做"更新"操作有效。对已存在的不符合校验规则的"文档"无效。
     * * 执行策略：
     *   - error：(默认值) 文档必须满足校验规则，才能被写入。
     *   - warn：对于"文档"不符合校验规则的 MongoDB 允许写入，但会记录一条告警到 mongod.log 中去。日志内容记录报错信息以及该"文档"的完整记录。
     *
     * @return 创建集合结果
     */
    public Object createCollectionValidation() {
        // 设置集合名称
        String collectionName = "users30";
        // 设置验证条件,只允许岁数大于20的用户信息插入
        CriteriaDefinition criteria = Criteria.where("age").gt(20);
        // 设置集合选项验证对象
        CollectionOptions collectionOptions = CollectionOptions.empty()
                .validator(Validator.criteria(criteria))
                // 设置效验级别
                .strictValidation()
                // 设置效验不通过后执行的动作
                .failOnValidationError();
        // 执行创建集合
        mongoTemplate.createCollection(collectionName, collectionOptions);
        // 检测新的集合是否存在，返回创建结果
        return mongoTemplate.collectionExists(collectionName) ? "创建集合成功" : "创建集合失败";
    }

}
