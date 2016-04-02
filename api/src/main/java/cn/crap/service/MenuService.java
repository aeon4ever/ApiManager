package cn.crap.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.crap.framework.Pick;
import cn.crap.framework.base.IBaseDao;
import cn.crap.framework.base.BaseService;
import cn.crap.inter.service.IErrorService;
import cn.crap.inter.service.IMenuService;
import cn.crap.inter.service.IModuleService;
import cn.crap.inter.service.IRoleService;
import cn.crap.inter.service.IUserService;
import cn.crap.model.Error;
import cn.crap.model.Menu;
import cn.crap.model.Module;
import cn.crap.model.Role;
import cn.crap.utils.Const;
import cn.crap.utils.DataType;
import cn.crap.utils.InterfaceStatus;
import cn.crap.utils.MenuType;
import cn.crap.utils.RequestMethod;
import cn.crap.utils.SettingType;
import cn.crap.utils.Tools;
import cn.crap.utils.TrueOrFalse;

@Service
public class MenuService extends
		BaseService<Menu> implements IMenuService {
	@Autowired
	private IModuleService moduleService;
	@Autowired
	private IErrorService errorService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;

	@Resource(name = "menuDao")
	public void setDao(IBaseDao<Menu> dao) {
		super.setDao(dao);
	}

	@Override
	public String pick(List<Pick> picks, String radio, String code, String key, String def) {
		Pick pick = null;
		if (radio.equals("true")) {
			pick = new Pick("pick_null", "", "------请选择-----");
			picks.add(pick);
		}
		switch (code) {
		case "MENU":
			pick = new Pick("0", "顶级类");
			picks.add(pick);
			for (Menu m : findByMap(Tools.getMap("parentId", "0"),
					null, null)) {
				pick = new Pick(m.getMenuId(), m.getMenuName());
				picks.add(pick);
			}
			break;
		case "AUTH":
			// 分割线
			pick = new Pick(Const.SEPARATOR, "模块管理");
			picks.add(pick);
			moduleService.getModulePick(picks, "m_", "0", "",DataType.MODULE.name()+"_moduleId","--【模块】");
			// 分割线
			pick = new Pick(Const.SEPARATOR, "接口管理");
			picks.add(pick);
			moduleService.getModulePick(picks, "i_", "0", "",DataType.INTERFACE.name()+"_moduleId","--【接口】");
			// 分割线
			pick = new Pick(Const.SEPARATOR, "错误码管理");
			picks.add(pick);
			for (Module m : moduleService.findByMap(
					Tools.getMap("parentId", "0"), null, null)) {
				pick = new Pick(m.getModuleId(),DataType.ERROR.name()+"_"+m.getModuleId(), m.getModuleName()+"--【错误码】");
				picks.add(pick);
			}
			// 分割线
			pick = new Pick(Const.SEPARATOR, "用户、菜单、角色管理");
			picks.add(pick);
			pick = new Pick(DataType.USER.name(), "用户管理");
			picks.add(pick);
			pick = new Pick(DataType.ROLE.name(), "角色管理");
			picks.add(pick);
			pick = new Pick(DataType.MENU.name(), "菜单管理");
			picks.add(pick);
			// 分割线
			pick = new Pick(Const.SEPARATOR, "我的菜单");
			picks.add(pick);
			for (Menu m : findByMap(
				Tools.getMap("parentId", "0","type",MenuType.BACK.name()), null, null)) {
				pick = new Pick(m.getMenuId(), m.getMenuName()+"--【菜单】");
				picks.add(pick);
			}
			break;
		case "ROLE":
			pick = new Pick(Const.SUPER, "超级管理员");
			picks.add(pick);
			for (Role r : roleService.findByMap(null,null, null)) {
				pick = new Pick(r.getRoleId(), r.getRoleName());
				picks.add(pick);
			}
			break;
		case "TOPMODULE":// 顶级模块
			pick = new Pick("0", "顶级类");
			picks.add(pick);
			for (Module m : moduleService.findByMap(
					Tools.getMap("parentId", "0"), null, null)) {
				pick = new Pick(m.getModuleId(), m.getModuleName());
				picks.add(pick);
			}
			break;
		case "INTERFACESTATUS":// 枚举 接口状态
			for (InterfaceStatus status : InterfaceStatus.values()) {
				pick = new Pick(status.getName(), status.name());
				picks.add(pick);
			}
			break;
		case "MENUTYPE":// 枚举 接口状态
			for (MenuType type : MenuType.values()) {
				pick = new Pick(type.name(), type.getName());
				picks.add(pick);
			}
			break;
		case "SETTINGTYPE":// 枚举 设置类型
			for (SettingType type : SettingType.values()) {
				pick = new Pick(type.name(), type.getName());
				picks.add(pick);
			}
			break;
		case "DATATYPE":// 枚举 数据类型
			for (DataType status : DataType.values()) {
				pick = new Pick(status.name(), status.getName());
				picks.add(pick);
			}
			break;
		case "REQUESTMETHOD": // 枚举 请求方式 post get
			for (RequestMethod status : RequestMethod.values()) {
				pick = new Pick(status.name(), status.getName(),
						status.getName());
				picks.add(pick);
			}
			break;
		case "TRUEORFALSE":// 枚举true or false
			for (TrueOrFalse status : TrueOrFalse.values()) {
				pick = new Pick(status.getName(), status.name());
				picks.add(pick);
			}
			break;
		case "ERRORCODE":// 错误码
			Module module = moduleService.get(key);
			while (module != null && !module.getParentId().equals("0")) {
				module = moduleService.get(module.getParentId());
			}
			for (Error error : errorService.findByMap(
					Tools.getMap("moduleId",
							module == null ? "" : module.getModuleId()), null,
					"errorCode asc")) {
				pick = new Pick(error.getErrorCode(), error.getErrorCode()
						+ "--" + error.getErrorMsg());
				picks.add(pick);
			}
			break;
		case "MENURUL":// 前段菜单显示模块url
			// 分割线
			pick = new Pick();
			pick.setName("前端错误码");
			pick.setValue(Const.SEPARATOR);
			picks.add(pick);
			String preUrl = "web.do#/webError/list/";
			for (Module m : moduleService.findByMap(
					Tools.getMap("parentId", "0"), null, null)) {
				pick = new Pick("e_" + m.getModuleId(), preUrl
						+ m.getModuleId(), m.getModuleName());
				picks.add(pick);
			}
			// 分割线
			pick = new Pick();
			pick.setName("前端模块");
			pick.setValue(Const.SEPARATOR);
			picks.add(pick);
			preUrl = "web.do#/webInterface/list/";
			moduleService.getModulePick(picks, "w_", "0", "",preUrl+"moduleId/moduleName");
			
			// 分割线
			pick = new Pick(Const.SEPARATOR, "后台");
			picks.add(pick);
			// 后端错误码管理
			pick = new Pick("h_e_0", "index.do#/error/list", "错误码列表");
			picks.add(pick);
			// 后端用户管理
			pick = new Pick("h_u_0", "index.do#/user/list", "用户列表");
			picks.add(pick);
			// 后端角色管理
			pick = new Pick("h_r_0", "index.do#/role/list", "角色列表");
			picks.add(pick);
			// 后端菜单管理
			pick = new Pick("h_m_0", "index.do#/menu/list/0/无", "菜单列表");
			picks.add(pick);
			// 分割线
			pick = new Pick(Const.SEPARATOR, "后台模块");
			picks.add(pick);
			// 后端接口&模块管理
			preUrl = "index.do#/interface/list/";
			pick = new Pick("h_0", preUrl + "0/无", "顶级模块");
			picks.add(pick);
			moduleService.getModulePick(picks, "h_", "0", "- - - ",preUrl+"moduleId/moduleName");
			
			break;
		case "ALLMODULE":// 所有模块
			moduleService.getModulePick(picks, "", "0", "",null);
			break;
		case "LEAFMODULE":// 查询叶子模块
			for (Module m : moduleService
					.findByHql("from ModuleModel m where m.moduleId not in (select m2.parentId from ModuleModel m2)")) {
				pick = new Pick(m.getModuleId(), m.getModuleName());
				picks.add(pick);
			}
			break;
		}
		/***********************组装字符串***************************/
		if(!radio.equals("")){
			StringBuilder pickContent = new StringBuilder();
			String separator = "<div class='separator'>%s</div>";
			String radioDiv = "<div class='p5 tl cursor%s' id='d_%s' onclick=\"pickCheck('%s','true');\">"+
					"<input id='%s' type='radio' %s disabled name='cid' value='%s'> "+
					"&nbsp;&nbsp; <span class='cidName'>%s</span></div>";
			String checkBoxDiv = "<div class='p5 tl cursor%s' id='d_%s' onclick=\"pickCheck('%s');\">"+
					"<input id='%s' type='checkbox' %s disabled name='cid' value='%s'>"+
					"&nbsp;&nbsp; <span class='cidName'>%s</span><br></div>";
				
				for(Pick p:picks){
					if(p.getValue().equals(Const.SEPARATOR)){
						pickContent.append(String.format(separator, p.getName()));
					}else{
						if(radio.equals("true")){
							pickContent.append(String.format(radioDiv, def.equals(p.getValue())?" pickActive":"", p.getId(), p.getId(),p.getId(),
									def.equals(p.getValue())?"checked":"", p.getValue(),p.getName()));
						}else{
							pickContent.append(String.format(checkBoxDiv,(","+def).indexOf(","+p.getValue()+",")>=0?" pickActive":"",p.getId(),p.getId(),p.getId(),
									(","+def).indexOf(","+p.getValue()+",")>=0?"checked":"",p.getValue(),p.getName()));
						}
					}
				}
			return pickContent.toString();
		}
		return "";
	}

}
