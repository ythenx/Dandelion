/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/hd/Research/Offloading/program/git/src/com/symlab/dandelion/IOffloadingService.aidl
 */
package com.symlab.dandelion;
public interface IOffloadingService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.symlab.dandelion.IOffloadingService
{
private static final java.lang.String DESCRIPTOR = "com.symlab.dandelion.IOffloadingService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.symlab.dandelion.IOffloadingService interface,
 * generating a proxy if needed.
 */
public static com.symlab.dandelion.IOffloadingService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.symlab.dandelion.IOffloadingService))) {
return ((com.symlab.dandelion.IOffloadingService)iin);
}
return new com.symlab.dandelion.IOffloadingService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getMyStatus:
{
data.enforceInterface(DESCRIPTOR);
com.symlab.dandelion.Status _result = this.getMyStatus();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getMyDeviceId:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getMyDeviceId();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_registerNsd:
{
data.enforceInterface(DESCRIPTOR);
this.registerNsd();
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterNsd:
{
data.enforceInterface(DESCRIPTOR);
this.unregisterNsd();
reply.writeNoException();
return true;
}
case TRANSACTION_updateStatusTable:
{
data.enforceInterface(DESCRIPTOR);
this.updateStatusTable();
reply.writeNoException();
return true;
}
case TRANSACTION_displayStatusTable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.displayStatusTable();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_discoverNSD:
{
data.enforceInterface(DESCRIPTOR);
this.discoverNSD();
reply.writeNoException();
return true;
}
case TRANSACTION_stopDiscoverNSD:
{
data.enforceInterface(DESCRIPTOR);
this.stopDiscoverNSD();
reply.writeNoException();
return true;
}
case TRANSACTION_getStatusTableData:
{
data.enforceInterface(DESCRIPTOR);
com.symlab.dandelion.StatusTableData _result = this.getStatusTableData();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addTaskToQueue:
{
data.enforceInterface(DESCRIPTOR);
com.symlab.dandelion.OfflaodableMethod _arg0;
if ((0!=data.readInt())) {
_arg0 = com.symlab.dandelion.OfflaodableMethod.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.addTaskToQueue(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.symlab.dandelion.IOffloadingService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public com.symlab.dandelion.Status getMyStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.symlab.dandelion.Status _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMyStatus, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.symlab.dandelion.Status.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getMyDeviceId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMyDeviceId, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void registerNsd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_registerNsd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterNsd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unregisterNsd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateStatusTable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_updateStatusTable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String displayStatusTable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_displayStatusTable, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void discoverNSD() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_discoverNSD, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopDiscoverNSD() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopDiscoverNSD, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public com.symlab.dandelion.StatusTableData getStatusTableData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.symlab.dandelion.StatusTableData _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStatusTableData, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.symlab.dandelion.StatusTableData.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addTaskToQueue(com.symlab.dandelion.OfflaodableMethod om) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((om!=null)) {
_data.writeInt(1);
om.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addTaskToQueue, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getMyStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getMyDeviceId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerNsd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterNsd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_updateStatusTable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_displayStatusTable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_discoverNSD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_stopDiscoverNSD = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getStatusTableData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_addTaskToQueue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public com.symlab.dandelion.Status getMyStatus() throws android.os.RemoteException;
public long getMyDeviceId() throws android.os.RemoteException;
public void registerNsd() throws android.os.RemoteException;
public void unregisterNsd() throws android.os.RemoteException;
public void updateStatusTable() throws android.os.RemoteException;
public java.lang.String displayStatusTable() throws android.os.RemoteException;
public void discoverNSD() throws android.os.RemoteException;
public void stopDiscoverNSD() throws android.os.RemoteException;
public com.symlab.dandelion.StatusTableData getStatusTableData() throws android.os.RemoteException;
public void addTaskToQueue(com.symlab.dandelion.OfflaodableMethod om) throws android.os.RemoteException;
}
