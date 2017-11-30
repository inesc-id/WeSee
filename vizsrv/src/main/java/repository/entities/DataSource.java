package repository.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="dataSource")
public class DataSource implements Serializable {
    @Id
    @GeneratedValue
    public long id;
    @Column(unique = true, nullable = false)
    public String name;
    @OneToMany(mappedBy = "dataSource")
    public Set<Connection> connectionSet;
    @OneToMany(mappedBy = "dataSource")
    public Set<Host> hostSet;
}
