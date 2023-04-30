package com.badeling.msbot.repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.badeling.msbot.entity.SellAndBuy;

public interface SellAndBuyRepository extends CrudRepository<SellAndBuy, Long>{
	@Query(value = "select * from sell_and_buy where user_id = ?1 and type = ?2",nativeQuery=true)
	SellAndBuy findUserByIdAndType(String user_id,String type);
	
	@Query(value = "select * from sell_and_buy where type = ?1 order by time desc",nativeQuery=true)
	List<SellAndBuy> findSabByType(String type);
	
	@Query(value = "select * from sell_and_buy where time < ?1",nativeQuery=true)
	List<SellAndBuy> findSabByTime(String time);
	
	
}
