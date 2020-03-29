package io.github.mrproliu.sharding.jdbc.demo;

import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * @author MrPro
 * @description: 用于快速构建集群
 * @date 2020-03-29 14:17:00
 */
public class ClusterShardingSimpleDemo {

    public static void main(String[] args) throws IOException, SQLException {
        // 构建数据源
        final URL resources = ClusterShardingSimpleDemo.class.getResource("/cluster-sharding.yaml");
        final DataSource dataSource = YamlShardingDataSourceFactory.createDataSource(new File(resources.getPath()));

        // 插入数据
        try (final Connection connection = dataSource.getConnection()) {
            for (int i = 1; i <= 10; i++) {
                final PreparedStatement statement = connection.prepareStatement("insert into t_order(user_id, amount) values (?, ?)");
                statement.setInt(1, i);
                statement.setInt(2, new Random().nextInt(100));

                System.out.println("插入第" + i + "条数据，返回结果:" + statement.executeUpdate());
            }
        }

        // 查询数据
        try (final Connection connection = dataSource.getConnection()) {
            for (int i = 1; i <= 10; i++) {
                final PreparedStatement statement = connection.prepareStatement("select * from t_order where user_id=?");

                statement.setInt(1, i);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        final long orderId = resultSet.getLong("order_id");
                        final int userId = resultSet.getInt("user_id");
                        final int amount = resultSet.getInt("amount");
                        System.out.println("获取到数据: 订单编号:" + orderId + ", 用户编号:" + userId + ", 价格:" + amount);
                    }
                }
            }
        }
    }
}
