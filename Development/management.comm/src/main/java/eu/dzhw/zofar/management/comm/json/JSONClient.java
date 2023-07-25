/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package eu.dzhw.zofar.management.comm.json;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
public class JSONClient {
	/** The Constant INSTANCE. */
	private static final JSONClient INSTANCE = new JSONClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(JSONClient.class);
	private final JsonWriterFactory FACTORY;
	private final Map<String, Object> properties;
	/**
	 * Instantiates a new SSH client.
	 */
	private JSONClient() {
		super();
		properties = new HashMap<String, Object>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		FACTORY = Json.createWriterFactory(properties);
	}
	/**
	 * Gets the single instance of SSHClient.
	 * 
	 * @return single instance of SSHClient
	 */
	public static synchronized JSONClient getInstance() {
		return INSTANCE;
	}
	public JsonObject createObject(final Map<String, Object> structuredData) {
		if (structuredData == null)
			return null;
		if (structuredData.isEmpty())
			return null;
		return (JsonObject) createObjectHelper(structuredData);
	}
	private Object createObjectHelper(final Object structuredData) {
		if (structuredData == null)
			return null;
		if ((Map.class).isAssignableFrom(structuredData.getClass())) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			final Map<String, Object> tmp = (Map<String, Object>) structuredData;
			for (Map.Entry<String, Object> item : tmp.entrySet()) {
				final Object xx = createObjectHelper(item.getValue());
				if (xx == null)
					continue;
				if ((JsonValue.class).isAssignableFrom(xx.getClass())) {
					builder = builder.add(item.getKey(), (JsonValue) xx);
				} else if ((Number.class).isAssignableFrom(xx.getClass())) {
					if ((Integer.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add(item.getKey(), ((Integer) xx));
					} else if ((BigInteger.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add(item.getKey(), ((BigInteger) xx).intValue());
					} else if ((Double.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add(item.getKey(), ((Double) xx));
					} else if ((Float.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add(item.getKey(), ((Float) xx));
					} else {
						LOGGER.error("Unhandeled Number Type {}", xx.getClass());
					}
				} else {
					builder = builder.add(item.getKey(), xx + "");
				}
			}
			return builder.build();
		} else if ((Object[].class).isAssignableFrom(structuredData.getClass())) {
			final Object[] tmp = (Object[]) structuredData;
			final int count = tmp.length;
			if ((count == 1)&&false) {
				return tmp[0];
			} else {
				JsonArrayBuilder builder = Json.createArrayBuilder();
				for (int a = 0; a < count; a++) {
					final Object xx = createObjectHelper(tmp[a]);
					if (xx == null)
						continue;
					if ((JsonValue.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add((JsonValue) xx);
					} else if ((Number.class).isAssignableFrom(xx.getClass())) {
						if ((Integer.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Integer) xx));
						} else if ((BigInteger.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((BigInteger) xx).intValue());
						} else if ((Double.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Double) xx));
						} else if ((Float.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Float) xx));
						} else {
							LOGGER.error("Unhandeled Number Type {}", xx.getClass());
						}
					} else {
						builder = builder.add(xx + "");
					}
				}
				return builder.build();
			}
		} else if ((Collection.class).isAssignableFrom(structuredData.getClass())) {
			final Collection<?> tmp = (Collection<?>) structuredData;
			if ((tmp.size() == 1)&&false) {
				return tmp.iterator().next();
			} else {
				JsonArrayBuilder builder = Json.createArrayBuilder();
				for (final Object item : tmp) {
					final Object xx = createObjectHelper(item);
					if (xx == null)
						continue;
					if ((JsonValue.class).isAssignableFrom(xx.getClass())) {
						builder = builder.add((JsonValue) xx);
					} else if ((Number.class).isAssignableFrom(xx.getClass())) {
						if ((Integer.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Integer) xx));
						} else if ((BigInteger.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((BigInteger) xx).intValue());
						} else if ((Double.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Double) xx));
						} else if ((Float.class).isAssignableFrom(xx.getClass())) {
							builder = builder.add(((Float) xx));
						} else {
							LOGGER.error("Unhandeled Number Type {}", xx.getClass());
						}
					} else {
						builder = builder.add(xx + "");
					}
				}
				return builder.build();
			}
		} else {
			return structuredData;
		}
	}
	public String writeToString(final JsonObject obj) {
		if(obj == null)return null;
		final StringWriter stWriter = new StringWriter();
		final JsonWriter jsonWriter = FACTORY.createWriter(stWriter);
		jsonWriter.writeObject(obj);
		jsonWriter.close();
		final String jsonData = stWriter.toString();
		return jsonData;
	}
	public boolean transferToRest(final String urlStr,final JsonObject payload) throws IOException {
		if(urlStr == null)return false;
		if(urlStr.contentEquals(""))return false;
		if(payload == null)return false;
		System.out.println("connect to : "+urlStr+ " Payload : "+payload.toString());
		final Map<String,String> requestParameter = new HashMap<String, String>();
		requestParameter.put("Content-Type", "application/json");
		HTTPClient.getInstance().post(urlStr,requestParameter , payload.toString());
		return true;
	}
}
