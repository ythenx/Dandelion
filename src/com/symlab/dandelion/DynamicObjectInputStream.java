package com.symlab.dandelion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import dalvik.system.DexClassLoader;

/**
 * Custom object input stream to also deal with dynamically loaded classes. The
 * classes can be retrieved from Android Dex files, provided in Apk (android
 * application) files.
 * 
 * @author Andrius
 * 
 */
public class DynamicObjectInputStream extends ObjectInputStream {

	private ClassLoader mCurrent = ClassLoader.getSystemClassLoader();
	private DexClassLoader mCurrentDexLoader = null;

	public DynamicObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	/**
	 * Override the method resolving a class to also look into the constructed
	 * DexClassLoader
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		try {
			try {
				return mCurrent.loadClass(desc.getName());
			} catch (ClassNotFoundException e) {
				return mCurrentDexLoader.loadClass(desc.getName());
			}
		} catch (ClassNotFoundException e) {
			return super.resolveClass(desc);
		} catch (NullPointerException e) { // Thrown when currentDexLoader is
			// not yet set up
			return super.resolveClass(desc);
		}

	}

	/**
	 * Add a Dex file to the Class Loader for dynamic class loading for clients
	 * 
	 * @param apkFile
	 *            the apk package
	 */
	public void addDex(final File apkFile) {
		if (mCurrentDexLoader == null)
			mCurrentDexLoader = new DexClassLoader(apkFile.getAbsolutePath(),
					apkFile.getParentFile().getAbsolutePath(), null, mCurrent);
		else
			mCurrentDexLoader = new DexClassLoader(apkFile.getAbsolutePath(),
					apkFile.getParentFile().getAbsolutePath(), null,
					mCurrentDexLoader);

	}

}
