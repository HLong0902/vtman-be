package com.viettel.vtman.cms.dao.impl;

import com.viettel.vtman.cms.base.dao.BaseFWDAOImpl;
import com.viettel.vtman.cms.controller.MenuController;
import com.viettel.vtman.cms.dao.MenuDAO;
import com.viettel.vtman.cms.dto.MenuDTO;
import com.viettel.vtman.cms.entity.Menu;
import com.viettel.vtman.cms.utils.Common;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class MenuDAOImpl extends BaseFWDAOImpl<Menu>  implements MenuDAO {


    @Override
    public List<Menu> getMenu(String searchKeyword, int page, int pageSize) {
        StringBuilder sqlCount = new StringBuilder();
        sqlCount.append("SELECT COUNT(*) FROM ");
        sqlCount.append(Menu.class.getName());
        sqlCount.append(" t WHERE upper(t.menuName) LIKE :key ");
        Query count = this.getSession().createQuery(sqlCount.toString());
        count.setParameter("key", "%" + searchKeyword + "%");
        MenuController.setCount(count.list().get(0));
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("FROM ");
        sqlSelect.append(Menu.class.getName());
        sqlSelect.append(" t WHERE upper(t.menuName) LIKE :key ORDER BY t.numberOrder, t.menuName");
        Query query = this.getSession().createQuery(sqlSelect.toString());
        query.setParameter("key", "%" + Common.escapeStringForMySQL(searchKeyword.trim().toUpperCase()) + "%");
        query.setFirstResult((page-1)*pageSize);
        query.setMaxResults(pageSize);
        return query.list();
    }

    @Override
    public Menu getMenuById(Long menuId) {
        if (this.findByProperties(Menu.class, "menuId", menuId).isEmpty()){
            return null;
        }
        return this.findByProperties(Menu.class, "menuId", menuId).get(0);
    }


    @Override
    public String create(Menu menu) {
        String name = menu.getMenuName();
        Long id = menu.getMenuId();
        if (notDuplicate(name, id)) return this.save(menu);
        else return "Dữ liệu đã tồn tại.";
    }
    @Override
    public String edit(Menu menu) {
        String name = menu.getMenuName();
        Long id = menu.getMenuId();
        if (notDuplicate(name, id)) return this.update(menu);
        else return "Dữ liệu đã tồn tại.";    }

    private boolean notDuplicate(String name, Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT 1 FROM ");
        hql.append(Menu.class.getName());
        hql.append(" t WHERE t.menuName = :name AND t.menuId != :id");
        Query query = this.getSession().createQuery(hql.toString());
        query.setParameter("name", name);
        query.setParameter("id", id);
        query.setMaxResults(1);
        return query.list().isEmpty();
    }
    @Override
    public void deleteById(Long menuId) {
        this.deleteById(menuId, Menu.class, "menuId");
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Menu> findAll(){
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("SELECT m.MENU_ID as menuId, m.MENU_NAME as menuName ");
        sqlSelect.append("FROM MENU m ");
        sqlSelect.append("order by m.MENU_NAME ");

        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sqlSelect.toString());

        q.addScalar("menuId", LongType.INSTANCE);
        q.addScalar("menuName", StringType.INSTANCE);

        q.setResultTransformer(Transformers.aliasToBean(Menu.class));
        List<Menu> list =  q.getResultList();

        return list;
    }

    @Override
    public List<MenuDTO> checkDuplicate(MenuDTO menuDTO) {
        StringBuilder sql = new StringBuilder();
        if (Objects.nonNull(menuDTO)){
            sql.append("SELECT ");
            sql.append(" m.MENU_ID as menuId, " );
            sql.append(" m.MENU_NAME as menuName, " );
            sql.append(" m.MENU_PATH menuPath, " );
            sql.append(" m.DESCRIPTION as description, " );
            sql.append(" m.ICON as icon, " );
            sql.append(" m.STATUS as status " );
            sql.append(" FROM ");
            sql.append(" MENU m ");
            sql.append(" WHERE ");
            sql.append(" binary upper(m.MENU_NAME) = upper(:menuName) ");
            SQLQuery query = (SQLQuery) entityManager.createNativeQuery(sql.toString());

            query.setParameter("menuName", Common.escapeStringForMySQL(menuDTO.getMenuName()));

            query.addScalar("menuId", LongType.INSTANCE);
            query.addScalar("menuName", StringType.INSTANCE);
            query.addScalar("menuPath", StringType.INSTANCE);
            query.addScalar("description", StringType.INSTANCE);
            query.addScalar("icon", StringType.INSTANCE);
            query.addScalar("status", LongType.INSTANCE);
            query.setResultTransformer(Transformers.aliasToBean(MenuDTO.class));
            List<MenuDTO> listResult = query.getResultList();
            return listResult;
        }
        return null;
    }

    @Override
    public MenuDTO findByMenuPath (String menuPath) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append(" m.MENU_ID as menuId, ");
        sql.append(" m.MENU_NAME as menuName, ");
        sql.append(" m.MENU_PATH menuPath ");
        sql.append(" FROM MENU m ");
        sql.append("WHERE binary upper(m.MENU_PATH) = upper(:menuPath) ");
        SQLQuery q = (SQLQuery) entityManager.createNativeQuery(sql.toString());
        q.setParameter("menuPath", Common.escapeStringForMySQL(menuPath));
        q.addScalar("menuId", LongType.INSTANCE);
        q.addScalar("menuName", StringType.INSTANCE);
        q.addScalar("menuPath", StringType.INSTANCE);
        q.setResultTransformer(Transformers.aliasToBean(MenuDTO.class));
        List<MenuDTO> list = q.getResultList();
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        this.update(menu);
        return menu;
    }
}
