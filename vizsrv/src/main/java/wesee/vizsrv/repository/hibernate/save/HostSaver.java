package wesee.vizsrv.repository.hibernate.save;

import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;
import wesee.vizsrv.repository.repository.HostRepository;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public class HostSaver {
    private HostRepository repo;

    public HostSaver(HostRepository repo) {
        this.repo = repo;
    }

    public EntitySaveResult<Host> findOrCreateHost(DataSource dataSource,
                                                   interception.models.connection_models.Host interceptionModel)
    {
        Host result = repo.findFirstByIpAndDataSource(interceptionModel.ip, dataSource);
        RepositorySaveResult.SaveStateEnum saveStateEnum = RepositorySaveResult.SaveStateEnum.UNCHANGED;
        if (result == null)
        {
            result = new Host();
            result.ip = interceptionModel.ip;
            result.dns = interceptionModel.dns;
            result.dataSource = dataSource;
            result.isIpv4 = interceptionModel.version == interception.models.connection_models.Host.IpVersion.IPV4? 1:0;
            result = repo.save(result);
            saveStateEnum = RepositorySaveResult.SaveStateEnum.NEW;
        }
        else
        {
            if (result.dns != interceptionModel.dns ||
                    (!((result.isIpv4 == 1) &&
                            (interceptionModel.version == interception.models.connection_models.Host.IpVersion.IPV4))))
            {
                result = repo.save(result);
                saveStateEnum = RepositorySaveResult.SaveStateEnum.CHANGED;
            }

        }
        return new EntitySaveResult(result, saveStateEnum);
    }
}
