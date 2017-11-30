package repository.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="connectionOccurrence")
public class ConnectionOccurrence implements Serializable {
    @Id
    @GeneratedValue
    public long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name="connectionId", referencedColumnName = "id")
    public Connection connection;
    public String message;
    @NotNull
    public long timeMs;
}
