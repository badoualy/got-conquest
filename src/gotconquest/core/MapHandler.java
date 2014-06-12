package gotconquest.core;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MapHandler extends DefaultHandler {

	private List<Kingdom> kingdoms;
	private List<City> cities;
	private Kingdom currentKingdom;
	private City currentCity;

	public List<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public List<City> getCities() {
		return cities;
	}

	@Override
	public void startDocument() {
		kingdoms = new ArrayList<Kingdom>(2);
		cities = new ArrayList<City>(6);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("kingdom")) {
			String name = atts.getValue("name");
			int reinforcement = Integer.parseInt(atts.getValue("reinforcements"));

			currentKingdom = new Kingdom(name, reinforcement);
		} else if (localName.equals("city")) {
			String name = atts.getValue("name");
			int x = Integer.parseInt(atts.getValue("x"));
			int y = Integer.parseInt(atts.getValue("y"));

			currentCity = new City(name, x, y);
		} else if (localName.equals("front")) {
			int sourceId = Integer.parseInt(atts.getValue("source"));
			int targetId = Integer.parseInt(atts.getValue("target"));
			City source = cities.get(sourceId);
			City target = cities.get(targetId);

			source.addFront(target, targetId);
			target.addFront(source, sourceId);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("kingdom")) {
			kingdoms.add(currentKingdom);
		} else if (localName.equals("city")) {
			currentKingdom.addCity(currentCity);
			cities.add(currentCity);
		}
	}
}
