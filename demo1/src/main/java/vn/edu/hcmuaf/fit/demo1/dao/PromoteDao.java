package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.News;
import vn.edu.hcmuaf.fit.demo1.model.Promote;

import java.util.List;

public class PromoteDao extends BaseDao{
    public List<Promote> getActiveProm(){
        return get().withHandle(handle -> handle.createQuery("Select * from promotions where is_active = TRUE ORDER BY promotion_date DESC LIMIT 3")
                .mapToBean(Promote.class).list()
        );
    }
    public List<Promote> getPromPagni(int limit, int offset){
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM promotions WHERE is_active = TRUE ORDER BY promotion_date DESC LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .mapToBean(Promote.class)
                        .list()
        );
    }
    public int countPromotions(){
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM promotions WHERE is_active = TRUE")
                        .mapTo(Integer.class)
                        .one()
        );
    }
}
