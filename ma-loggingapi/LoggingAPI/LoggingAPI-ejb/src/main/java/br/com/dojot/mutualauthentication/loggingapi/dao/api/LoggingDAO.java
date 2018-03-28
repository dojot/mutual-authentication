package br.com.dojot.mutualauthentication.loggingapi.dao.api;

import java.util.Date;
import java.util.List;

import br.com.dojot.mutualauthentication.loggingapi.beans.entity.LoggingVO;

public interface LoggingDAO {

	void save(LoggingVO vo);
	
	List<LoggingVO> searchLoggingToClear(Date date);
	
	List<LoggingVO> searchLoggingPeriod(Date init, Date end);
}
