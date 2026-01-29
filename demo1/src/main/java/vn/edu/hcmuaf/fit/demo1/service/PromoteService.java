package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.PromoteDao;
import vn.edu.hcmuaf.fit.demo1.model.Promote;

import java.util.List;

public class PromoteService {
    private final PromoteDao promoteDao = new PromoteDao();

    public List<Promote> getActiveProm(){
        return promoteDao.getActiveProm();
    }
    public List<Promote> getPromPagni(int limit, int offset){
        return promoteDao.getPromPagni(limit,offset);
    }
    public int countPromotions(){
        return promoteDao.countPromotions();
    }
}
