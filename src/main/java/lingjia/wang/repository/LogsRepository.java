package lingjia.wang.repository;

import lingjia.wang.model.domain.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2018/1/19
 * @version : 1.0
 */
public interface LogsRepository extends JpaRepository<Logs,Long> {

    /**
     * 查询最新的五条数据
     *
     * @return list
     */
    @Query(value = "SELECT * FROM cms_logs ORDER BY log_created DESC LIMIT 5",nativeQuery = true)
    List<Logs> findTopFive();
}
