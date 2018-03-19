package sunwou.serviceimple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;


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
		return fileDao.add(file);
	}

	@Override
	public int remove(String[] ids) {
		Update update=new Update();
		update.set("isDelete", true);
		int rs=fileDao.getMongoTemplate().
				updateMulti(new Query(Criteria.where("sunwouId").in(ids)), update, FileEntity.tableName).getN();
		return rs;
	}

	@Override
	public List<FileEntity> fileList(QueryObject qo) {
		return fileDao.find(qo);
	}

	@Override
	public Long userSize(String userId) {
		return null;
	}
	
	
}
