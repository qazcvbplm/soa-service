package sunwou.serviceimple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;

import com.mongodb.operation.AggregateOperation;

import sunwou.entity.FileEntity;
import sunwou.mongo.util.MongoBaseDaoImple;
import sunwou.mongo.util.QueryObject;
import sunwou.service.IFileService;

@Component("FileServiceImple")
public class FileServiceImple implements IFileService{

	@Autowired
	private MongoBaseDaoImple<FileEntity> fileDao;

	@Override
	public String add(FileEntity file) {
		// TODO Auto-generated method stub
		return fileDao.add(file);
	}

	@Override
	public int remove(String[] ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<FileEntity> fileList(QueryObject qo) {
		// TODO Auto-generated method stub
		return fileDao.find(qo);
	}

	@Override
	public Long userSize(String userId) {
		
		return null;
	}
	
	
}
