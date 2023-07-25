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
package eu.dzhw.zofar.management.calc.geo;
import java.awt.geom.Point2D;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import com.gargoylesoftware.htmlunit.Page;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.utils.objects.CollectionClient;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public class GoogleMapsClient extends AbstractGeoClient {
	private static final long serialVersionUID = 2471691522327642813L;
	private final String KEY;
	/** The instance. */
	private static GoogleMapsClient INSTANCE;
	private GoogleMapsClient(final String googleKey) {
		super();
		if (googleKey == null) {
			KEY = "";
		} else if (googleKey.equals("")) {
			KEY = "";
		} else
			KEY = "key=" + googleKey + "&";
	}
	public String getKEY() {
		return KEY;
	}
	/**
	 * Gets the single instance of JSONService.
	 * 
	 * @return single instance of JSONService
	 */
	public static GoogleMapsClient getInstance(final String googleKey) {
		if (INSTANCE != null) {
			if ((googleKey == null) && (INSTANCE.getKEY().equals(""))) {
			} else if ((googleKey.equals("")) && (INSTANCE.getKEY().equals(""))) {
			} else if (INSTANCE.getKEY().equals("key=" + googleKey + "&")) {
			} else
				INSTANCE = null;
		}
		if (INSTANCE == null)
			INSTANCE = new GoogleMapsClient(googleKey);
		return INSTANCE;
	}
	public Double getDistanceFromWeb(final String place1, final String countryPlace1, final String place2, final String countryPlace2) throws Exception {
		Point2D place1Location = getLocationFromWeb(place1, countryPlace1);
		Point2D place2Location = getLocationFromWeb(place2, countryPlace2);
		double distance = distanceKM(place1Location, place2Location);
		return distance;
	}
	@Deprecated
	private Point2D getLocationFromWeb(final String place, final String country) throws Exception {
		final HTTPClient spider = HTTPClient.getInstance();
		spider.setJavaScriptEnabled(false);
		Page locationPage = spider.loadPage("https://maps.googleapis.com/maps/api/geocode/xml?" + KEY + "address=" + URLEncoder.encode(place, "UTF-8") + "&components=country:" + country + "&sensor=false");
		XmlClient xmlClient = XmlClient.getInstance();
		if ((locationPage != null) && ((com.gargoylesoftware.htmlunit.xml.XmlPage.class).isAssignableFrom(locationPage.getClass()))) {
			final Document doc = ((com.gargoylesoftware.htmlunit.xml.XmlPage) locationPage).getXmlDocument();
			final XmlObject result = XmlClient.getInstance().docToXmlObject(doc);
			Double latValue = null;
			Double lngValue = null;
			XmlObject[] lat = xmlClient.getByXPath(result, "descendant::geometry/location/lat");
			if ((lat != null) && (lat.length == 1)) {
				latValue = Double.parseDouble(lat[0].newCursor().getTextValue());
			}
			XmlObject[] lng = xmlClient.getByXPath(result, "descendant::geometry/location/lng");
			if ((lng != null) && (lng.length == 1)) {
				lngValue = Double.parseDouble(lng[0].newCursor().getTextValue());
			}
			if ((latValue != null) && (lngValue != null))
				return new Point2D.Double(latValue, lngValue);
		}
		return null;
	}
	public Point2D getLocation(final Object location) throws Exception {
		XmlClient xmlClient = XmlClient.getInstance();
		Double latValue = null;
		Double lngValue = null;
		XmlObject[] lat = xmlClient.getByXPath((XmlObject) location, "descendant::geometry/location/lat");
		if ((lat != null) && (lat.length == 1)) {
			latValue = Double.parseDouble(lat[0].newCursor().getTextValue());
		}
		XmlObject[] lng = xmlClient.getByXPath((XmlObject) location, "descendant::geometry/location/lng");
		if ((lng != null) && (lng.length == 1)) {
			lngValue = Double.parseDouble(lng[0].newCursor().getTextValue());
		}
		if ((latValue != null) && (lngValue != null))
			return new Point2D.Double(latValue, lngValue);
		return null;
	}
	public Map<String, String> getAdressComponents(final XmlObject location) throws Exception {
		XmlClient xmlClient = XmlClient.getInstance();
		XmlObject[] addressComponents = xmlClient.getByXPath(location, "descendant::address_component");
		if (addressComponents != null) {
			final Map<String, String> back = new LinkedHashMap<String, String>();
			for (XmlObject addressComponent : addressComponents) {
				XmlObject[] values = xmlClient.getByXPath(addressComponent, "./long_name");
				XmlObject[] types = xmlClient.getByXPath(addressComponent, "./type");
				if ((values != null) && (types != null)) {
					for (XmlObject type : types) {
						final List<Object> valueList = new ArrayList<Object>();
						for (XmlObject value : values) {
							valueList.add(value.newCursor().getTextValue());
						}
						back.put(type.newCursor().getTextValue(), CollectionClient.getInstance().implode(valueList));
					}
				}
			}
			return back;
		}
		return null;
	}
	public XmlObject getObjectByPostcode(final Object postcode, final String country) throws Exception {
		final HTTPClient spider = HTTPClient.getInstance();
		spider.setJavaScriptEnabled(false);
		Page locationPage = spider.loadPage("https://maps.googleapis.com/maps/api/geocode/xml?" + KEY + "components=country:" + country + "|postal_code:" + postcode + "&sensor=false");
		if ((locationPage != null) && ((com.gargoylesoftware.htmlunit.xml.XmlPage.class).isAssignableFrom(locationPage.getClass()))) {
			final Document doc = ((com.gargoylesoftware.htmlunit.xml.XmlPage) locationPage).getXmlDocument();
			final XmlObject result = XmlClient.getInstance().docToXmlObject(doc);
			if (result != null) {
				XmlObject[] status = XmlClient.getInstance().getByXPath(result, "descendant::status");
				if ((status != null) && (status.length == 1)) {
					final String statusMessage = status[0].newCursor().getTextValue();
					if ((statusMessage != null) && (statusMessage.equals("OK")))
						return result;
					else
						throw new Exception(statusMessage + " for searching postcode " + postcode + " (" + locationPage.getUrl() + ")");
				}
			}
		}
		return null;
	}
	public XmlObject getObjectByAdress(final String adress, final String country) throws Exception {
		final HTTPClient spider = HTTPClient.getInstance();
		spider.setJavaScriptEnabled(false);
		Page locationPage = spider.loadPage("https://maps.googleapis.com/maps/api/geocode/xml?" + KEY + "address=" + URLEncoder.encode(adress, "UTF-8") + "&components=country:" + country + "&sensor=false");
		if ((locationPage != null) && ((com.gargoylesoftware.htmlunit.xml.XmlPage.class).isAssignableFrom(locationPage.getClass()))) {
			final Document doc = ((com.gargoylesoftware.htmlunit.xml.XmlPage) locationPage).getXmlDocument();
			final XmlObject result = XmlClient.getInstance().docToXmlObject(doc);
			if (result != null) {
				XmlObject[] status = XmlClient.getInstance().getByXPath(result, "descendant::status");
				if ((status != null) && (status.length == 1)) {
					final String statusMessage = status[0].newCursor().getTextValue();
					if ((statusMessage != null) && (statusMessage.equals("OK")))
						return result;
					else
						throw new Exception(statusMessage);
				}
			}
		}
		return null;
	}
	public enum NearByTypes {
		university, school, library;
	};
	public List<XmlObject> getNearBy(final XmlObject location, final NearByTypes type) throws Exception {
		final HTTPClient spider = HTTPClient.getInstance();
		spider.setJavaScriptEnabled(false);
		final Point2D coords = this.getLocation(location);
		Page locationPage = spider.loadPage("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?" + KEY + "location=" + coords.getX() + "," + coords.getY() + "&rankby=distance&type=" + type + " ");
//		Page locationPage = spider.loadPage("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?" + KEY + "location=" + coords.getX() + "," + coords.getY() + "&radius=50000&rankby=prominence&type=" + type + " ");
		if ((locationPage != null) && ((com.gargoylesoftware.htmlunit.xml.XmlPage.class).isAssignableFrom(locationPage.getClass()))) {
			final Document doc = ((com.gargoylesoftware.htmlunit.xml.XmlPage) locationPage).getXmlDocument();
			final XmlObject result = XmlClient.getInstance().docToXmlObject(doc);
			if (result != null) {
				XmlClient xmlClient = XmlClient.getInstance();
				XmlObject[] status = xmlClient.getByXPath(result, "descendant::status");
				if ((status != null) && (status.length == 1)) {
					final String statusMessage = status[0].newCursor().getTextValue();
					if ((statusMessage != null) && (statusMessage.equals("OK"))) {
						XmlObject[] resultItems = xmlClient.getByXPath(result, "descendant::result");
						if (resultItems != null) {
							final String typeStr = type.toString();
							final List<XmlObject> back = new ArrayList<XmlObject>();
							for (XmlObject resultItem : resultItems) {
								XmlObject[] types = xmlClient.getByXPath(resultItem, "./type");
								for (XmlObject resultType : types) {
									final String resultTypeStr = resultType.newCursor().getTextValue();
									if ((resultTypeStr != null) && (resultTypeStr.equals(typeStr))) {
										Document resultDoc = xmlClient.getDocumentFromString(xmlClient.convert2String(resultItem));
										if (!back.contains(resultDoc)) {
											back.add(xmlClient.docToXmlObject(resultDoc));
											break;
										}
									}
								}
							}
							return back;
						}
					} else
						throw new Exception(statusMessage + " for searching nearby " + type + " (" + locationPage.getUrl() + ")");
				}
			}
		}
		return null;
	}
}
