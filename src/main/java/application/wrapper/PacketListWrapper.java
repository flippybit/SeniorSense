package application.wrapper;

 

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import application.model.ArduPacket;

@XmlRootElement(name = "ArduPackets")
public class PacketListWrapper {


	  private List<ArduPacket> ArduPackets;

	    @XmlElement(name = "ArduPacket")
	    public List<ArduPacket> getArduPackets() {
	        return ArduPackets;
	    }

	    public void setArduPackets(List<ArduPacket> _arduPackets) {
	        this.ArduPackets = _arduPackets ;
	    }

	    public void setArduPacket(ArduPacket _arduPacket){
	    	if(this.ArduPackets == null ){

	    	}else{
	    	this.ArduPackets.add(_arduPacket);
	    	}
	   }
}
