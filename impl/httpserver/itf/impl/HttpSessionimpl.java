package httpserver.itf.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import httpserver.itf.HttpSession;

public class HttpSessionimpl implements HttpSession {
    private final String m_id;
    private final ConcurrentHashMap<String, Object> m_values;

    public HttpSessionimpl(String id) {
		m_id = id;
		m_values = new ConcurrentHashMap<String, Object>();
        
	}

    @Override
    public String getId() {
        return m_id;
    }

    @Override
    public Object getValue(String key) {
        synchronized (m_values) {
            return m_values.get(key);
        }
    }

    @Override
    public void setValue(String key, Object value) {
        synchronized (m_values) {
            m_values.put(key, value);
        }
    }
    
}
