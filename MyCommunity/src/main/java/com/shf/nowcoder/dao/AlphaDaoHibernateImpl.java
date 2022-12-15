package com.shf.nowcoder.dao;

import org.springframework.stereotype.Repository;

@Repository("AlphaDaoHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "AlphaDaoHibernateImpl";
    }
}
