package httpserver.itf.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import httpserver.itf.HttpSession;

public class HttpSessionimpl implements HttpSession {
    String Id;
    private Map<String,Object> m_values;

    public HttpSessionimpl(String id) {
		Id = id;
		m_values = new HashMap<String, Object>();
        
	}

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public Object getValue(String key) {
       return m_values.get(key);
    }

    @Override
    public void setValue(String key, Object value) {
        m_values.put(key, value);
    }
    
}
