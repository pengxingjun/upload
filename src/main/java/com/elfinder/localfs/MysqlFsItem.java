package com.elfinder.localfs;

import com.elfinder.services.FsItem;
import com.elfinder.services.FsVolume;
import com.web.entities.UploadFile;

import java.io.File;

public class MysqlFsItem implements FsItem
{
	UploadFile _file;

	FsVolume _volume;

	public MysqlFsItem(MysqlFsVolume volume, UploadFile file)
	{
		super();
		_volume = volume;
		_file = file;
	}

	public UploadFile getFile()
	{
		return _file;
	}

	public FsVolume getVolume()
	{
		return _volume;
	}

	public void setFile(UploadFile file)
	{
		_file = file;
	}

	public void setVolume(FsVolume volume)
	{
		_volume = volume;
	}

	@Override
	public String toString()
	{
		return "MysqlFsVolume [" + _file.getId() + "_" + _file.getName() + "]";
	}
}
