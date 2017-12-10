package wesee.vizsrv.repository.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataSource implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @Column(unique = true, nullable = false)
    public String name;
    @OneToMany(mappedBy = "dataSource")
    public List<Connection> connections = new ArrayList<>();
    @OneToMany(mappedBy = "dataSource")
    public List<Host> hosts = new ArrayList<>();
}
