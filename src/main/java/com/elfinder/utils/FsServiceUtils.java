package com.elfinder.utils;

import java.io.IOException;

import com.elfinder.controller.executor.FsItemEx;
import com.elfinder.services.FsItem;
import com.elfinder.services.FsService;


public abstract class FsServiceUtils
{
	public static FsItemEx findItem(FsService fsService, String hash)
			throws IOException
	{
		FsItem fsi = fsService.fromHash(hash);
		if (fsi == null)
		{
			return null;
		}

		return new FsItemEx(fsi, fsService);
	}
}
