
package example.ws.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{build.soapserver.ws}host"/>
 *         &lt;element name="destination" type="{build.soapserver.ws}host"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "source",
    "destination"
})
@XmlRootElement(name = "registerConnectionRequest")
public class RegisterConnectionRequest {

    @XmlElement(required = true)
    protected Host source;
    @XmlElement(required = true)
    protected Host destination;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Host }
     *     
     */
    public Host getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Host }
     *     
     */
    public void setSource(Host value) {
        this.source = value;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link Host }
     *     
     */
    public Host getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link Host }
     *     
     */
    public void setDestination(Host value) {
        this.destination = value;
    }

}
