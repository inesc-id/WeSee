package wesee.vizsrv.repository.hibernate.save;

import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.repository.DataSourceRepository;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public class DataSourceSaver {
    public DataSourceSaver(DataSourceRepository repo) {
        this.repo = repo;
    }

    private DataSourceRepository repo;

    public EntitySaveResult<DataSource> findOrCreateDataSaver(String name)
    {
        DataSource result = repo.findFirstByName(name);
        RepositorySaveResult.SaveStateEnum saveStateEnum = RepositorySaveResult.SaveStateEnum.UNCHANGED;
        if (result == null)
        {
            result = new DataSource();
            result.name = name;
            result = repo.save(result);
            saveStateEnum = RepositorySaveResult.SaveStateEnum.NEW;
        }
        return new EntitySaveResult(result, saveStateEnum);
    }
}
