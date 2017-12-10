package wesee.vizsrv.repository.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.Charset;

@Entity
public class ConnectionOccurrence implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name="connectionId", referencedColumnName = "id")
    public Connection connection;
    @Column(columnDefinition = "MEDIUMBLOB")
    public byte[] message;
    @Column(nullable = false)
    public long timeMs;

    public String getMessage()
    {
        return new String(message);
    }

    public void setMessage(String message)
    {
        this.message = message.getBytes();
    }
}
