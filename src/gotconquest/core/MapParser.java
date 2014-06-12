package gotconquest.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MapParser {
	private MapHandler handler = new MapHandler();
	private InputStream map;

	public MapParser(InputStream map) {
		this.map = map;
	}

	public void parse() throws ParserConfigurationException, SAXException, IOException {
		XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(map));
	}

	public List<Kingdom> getKingdoms() {
		return handler.getKingdoms();
	}

	public List<City> getCities() {
		return handler.getCities();
	}
}
