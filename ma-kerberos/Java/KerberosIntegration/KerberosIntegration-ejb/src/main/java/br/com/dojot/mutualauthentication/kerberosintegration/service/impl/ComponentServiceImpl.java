package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.cache.ComponentCache;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.entity.ComponentVO;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ParseException;
import br.com.dojot.mutualauthentication.kerberoslib.util.CryptoUtil;
import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ComponentDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ComponentService;

@Stateless
public class ComponentServiceImpl implements ComponentService {

	@Inject
	private ComponentDAO componentDAO;

	@Override
	public void save(ComponentVO vo) {
		JSONObject json = new JSONObject();
		json.put("ticketValidityPeriod", vo.getTicketValidityPeriod());
		json.put("componentId", vo.getComponentId());
		json.put("id", vo.getId());
		json.put("key", CryptoUtil.ByteArrayToHexStr(vo.getKey()));
		componentDAO.add(CryptoUtil.ByteArrayToHexStr(vo.getComponentId()), json.toString());
	}

	@Override
	public void remove(String componentId) {
		componentDAO.remove(componentId);
	}

	@Override
	public ComponentCache get(String componentId) {
		ComponentCache cache = null;
		try {
			String json = componentDAO.get(componentId);
			if (json != null) {
				JSONObject jsonObject = new JSONObject(json);
				cache = new ComponentCache();
				cache.setTicketValidityPeriod(jsonObject.getLong("ticketValidityPeriod"));
				cache.setKey(CryptoUtil.HexStrToByteArray(jsonObject.getString("key")));
			}
		} catch (JSONException e) {
			// TODO: tratar exception
			cache = null;
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO: tratar exception
			cache = null;
			e.printStackTrace();
		}
		return cache;
	}

}
