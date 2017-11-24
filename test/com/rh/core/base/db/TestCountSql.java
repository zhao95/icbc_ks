package com.rh.core.base.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class TestCountSql {

	@Test
	public void testCountSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT USER_CODE, DEPT_CODE, USER_NAME, USER_LOGIN_NAME, USER_IMG_SRC, USER_POST,");
		sql.append(" USER_SORT, USER_SHORT_NAME, USER_MOBILE, USER_EMAIL FROM SY_BASE_USER_V");
		sql.append(" WHERE 1 = 1  AND ODEPT_CODE = 'rh' AND S_FLAG = 1 AND CMPY_CODE = 'ruaho' ");
		sql.append(
				" ORDER BY USER_POST_LEVEL , USER_EDU_MAJOR, USER_SORT desc, NLSSORT (USER_NAME, 'NLS_SORT=SCHINESE_PINYIN_M') ");

		PlainSelect ps = parseSelect(sql.toString());

		Function count = new Function();
		count.setName("count");
		count.setAllColumns(true);
		// count.setParameters(list);

		SelectExpressionItem countItem = new SelectExpressionItem(count);

		Alias alias = new Alias("_COUNT", true);
		countItem.setAlias(alias);

		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(countItem);

		ps.setSelectItems(list);

		ps.setOrderByElements(new ArrayList<OrderByElement>());
		// selectItems 字段
		// orderByElements 排序

		System.out.println(ps.toString());
	}

	private PlainSelect parseSelect(String sql) {
		try {
			Select select = (Select) CCJSqlParserUtil.parse(sql);
			
			System.out.println(select.getClass());
			SelectBody selectBody = select.getSelectBody();
			if (selectBody instanceof PlainSelect) {
				PlainSelect ps = (PlainSelect) selectBody;
				return ps;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Test
	public void testCountSql2() {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) \"COUNT_\" from dual");

		PlainSelect select = parseSelect(sql.toString());

		System.out.println(select.toString());
	}
}
