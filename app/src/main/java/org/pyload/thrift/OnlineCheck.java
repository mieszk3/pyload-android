/**
 * Autogenerated by Thrift Compiler (0.8.0-dev)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package org.pyload.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class OnlineCheck implements org.apache.thrift.TBase<OnlineCheck, OnlineCheck._Fields>, java.io.Serializable, Cloneable {
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OnlineCheck");
    private static final org.apache.thrift.protocol.TField RID_FIELD_DESC = new org.apache.thrift.protocol.TField("rid", org.apache.thrift.protocol.TType.I32, (short) 1);
    private static final org.apache.thrift.protocol.TField DATA_FIELD_DESC = new org.apache.thrift.protocol.TField("data", org.apache.thrift.protocol.TType.MAP, (short) 2);
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    // isset id assignments
    private static final int __RID_ISSET_ID = 0;

    static {
        schemes.put(StandardScheme.class, new OnlineCheckStandardSchemeFactory());
        schemes.put(TupleScheme.class, new OnlineCheckTupleSchemeFactory());
    }

    static {
        Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.RID, new org.apache.thrift.meta_data.FieldMetaData("rid", org.apache.thrift.TFieldRequirementType.DEFAULT,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32, "ResultID")));
        tmpMap.put(_Fields.DATA, new org.apache.thrift.meta_data.FieldMetaData("data", org.apache.thrift.TFieldRequirementType.DEFAULT,
                new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING),
                        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, OnlineStatus.class))));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OnlineCheck.class, metaDataMap);
    }

    public int rid; // required
    public Map<String, OnlineStatus> data; // required
    private BitSet __isset_bit_vector = new BitSet(1);

    public OnlineCheck() {
    }

    public OnlineCheck(
            int rid,
            Map<String, OnlineStatus> data) {
        this();
        this.rid = rid;
        setRidIsSet(true);
        this.data = data;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public OnlineCheck(OnlineCheck other) {
        __isset_bit_vector.clear();
        __isset_bit_vector.or(other.__isset_bit_vector);
        this.rid = other.rid;
        if (other.isSetData()) {
            Map<String, OnlineStatus> __this__data = new HashMap<String, OnlineStatus>();
            for (Map.Entry<String, OnlineStatus> other_element : other.data.entrySet()) {

                String other_element_key = other_element.getKey();
                OnlineStatus other_element_value = other_element.getValue();

                String __this__data_copy_key = other_element_key;

                OnlineStatus __this__data_copy_value = new OnlineStatus(other_element_value);

                __this__data.put(__this__data_copy_key, __this__data_copy_value);
            }
            this.data = __this__data;
        }
    }

    public OnlineCheck deepCopy() {
        return new OnlineCheck(this);
    }

    @Override
    public void clear() {
        setRidIsSet(false);
        this.rid = 0;
        this.data = null;
    }

    public int getRid() {
        return this.rid;
    }

    public OnlineCheck setRid(int rid) {
        this.rid = rid;
        setRidIsSet(true);
        return this;
    }

    public void unsetRid() {
        __isset_bit_vector.clear(__RID_ISSET_ID);
    }

    /**
     * Returns true if field rid is set (has been assigned a value) and false otherwise
     */
    public boolean isSetRid() {
        return __isset_bit_vector.get(__RID_ISSET_ID);
    }

    public void setRidIsSet(boolean value) {
        __isset_bit_vector.set(__RID_ISSET_ID, value);
    }

    public int getDataSize() {
        return (this.data == null) ? 0 : this.data.size();
    }

    public void putToData(String key, OnlineStatus val) {
        if (this.data == null) {
            this.data = new HashMap<String, OnlineStatus>();
        }
        this.data.put(key, val);
    }

    public Map<String, OnlineStatus> getData() {
        return this.data;
    }

    public OnlineCheck setData(Map<String, OnlineStatus> data) {
        this.data = data;
        return this;
    }

    public void unsetData() {
        this.data = null;
    }

    /**
     * Returns true if field data is set (has been assigned a value) and false otherwise
     */
    public boolean isSetData() {
        return this.data != null;
    }

    public void setDataIsSet(boolean value) {
        if (!value) {
            this.data = null;
        }
    }

    public void setFieldValue(_Fields field, Object value) {
        switch (field) {
            case RID:
                if (value == null) {
                    unsetRid();
                } else {
                    setRid((Integer) value);
                }
                break;

            case DATA:
                if (value == null) {
                    unsetData();
                } else {
                    setData((Map<String, OnlineStatus>) value);
                }
                break;

        }
    }

    public Object getFieldValue(_Fields field) {
        switch (field) {
            case RID:
                return Integer.valueOf(getRid());

            case DATA:
                return getData();

        }
        throw new IllegalStateException();
    }

    /**
     * Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
     */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }

        switch (field) {
            case RID:
                return isSetRid();
            case DATA:
                return isSetData();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof OnlineCheck)
            return this.equals((OnlineCheck) that);
        return false;
    }

    public boolean equals(OnlineCheck that) {
        if (that == null)
            return false;

        boolean this_present_rid = true;
        boolean that_present_rid = true;
        if (this_present_rid || that_present_rid) {
            if (!(this_present_rid && that_present_rid))
                return false;
            if (this.rid != that.rid)
                return false;
        }

        boolean this_present_data = true && this.isSetData();
        boolean that_present_data = true && that.isSetData();
        if (this_present_data || that_present_data) {
            if (!(this_present_data && that_present_data))
                return false;
            return this.data.equals(that.data);
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public int compareTo(OnlineCheck other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;
        OnlineCheck typedOther = other;

        lastComparison = Boolean.valueOf(isSetRid()).compareTo(typedOther.isSetRid());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetRid()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rid, typedOther.rid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetData()).compareTo(typedOther.isSetData());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetData()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.data, typedOther.data);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
        schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
        schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OnlineCheck(");
        boolean first = true;

        sb.append("rid:");
        sb.append(this.rid);
        first = false;
        if (!first) sb.append(", ");
        sb.append("data:");
        if (this.data == null) {
            sb.append("null");
        } else {
            sb.append(this.data);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
        // check for required fields
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        try {
            write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te.getMessage());
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        try {
            // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
            __isset_bit_vector = new BitSet(1);
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te.getMessage());
        }
    }

    /**
     * The set of fields this struct contains, along with convenience methods for finding and manipulating them.
     */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        RID((short) 1, "rid"),
        DATA((short) 2, "data");

        private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

        static {
            for (_Fields field : EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        private final short _thriftId;
        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not found.
         */
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
                case 1: // RID
                    return RID;
                case 2: // DATA
                    return DATA;
                default:
                    return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null)
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not found.
         */
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    private static class OnlineCheckStandardSchemeFactory implements SchemeFactory {
        public OnlineCheckStandardScheme getScheme() {
            return new OnlineCheckStandardScheme();
        }
    }

    private static class OnlineCheckStandardScheme extends StandardScheme<OnlineCheck> {

        public void read(org.apache.thrift.protocol.TProtocol iprot, OnlineCheck struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // RID
                        if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                            struct.rid = iprot.readI32();
                            struct.setRidIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 2: // DATA
                        if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
                            {
                                org.apache.thrift.protocol.TMap _map50 = iprot.readMapBegin();
                                struct.data = new HashMap<String, OnlineStatus>(2 * _map50.size);
                                for (int _i51 = 0; _i51 < _map50.size; ++_i51) {
                                    String _key52; // required
                                    OnlineStatus _val53; // required
                                    _key52 = iprot.readString();
                                    _val53 = new OnlineStatus();
                                    _val53.read(iprot);
                                    struct.data.put(_key52, _val53);
                                }
                                iprot.readMapEnd();
                            }
                            struct.setDataIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    default:
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();

            // check for required fields of primitive type, which can't be checked in the validate method
            struct.validate();
        }

        public void write(org.apache.thrift.protocol.TProtocol oprot, OnlineCheck struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            oprot.writeFieldBegin(RID_FIELD_DESC);
            oprot.writeI32(struct.rid);
            oprot.writeFieldEnd();
            if (struct.data != null) {
                oprot.writeFieldBegin(DATA_FIELD_DESC);
                {
                    oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, struct.data.size()));
                    for (Map.Entry<String, OnlineStatus> _iter54 : struct.data.entrySet()) {
                        oprot.writeString(_iter54.getKey());
                        _iter54.getValue().write(oprot);
                    }
                    oprot.writeMapEnd();
                }
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class OnlineCheckTupleSchemeFactory implements SchemeFactory {
        public OnlineCheckTupleScheme getScheme() {
            return new OnlineCheckTupleScheme();
        }
    }

    private static class OnlineCheckTupleScheme extends TupleScheme<OnlineCheck> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, OnlineCheck struct) throws org.apache.thrift.TException {
            TTupleProtocol oprot = (TTupleProtocol) prot;
            BitSet optionals = new BitSet();
            if (struct.isSetRid()) {
                optionals.set(0);
            }
            if (struct.isSetData()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetRid()) {
                oprot.writeI32(struct.rid);
            }
            if (struct.isSetData()) {
                {
                    oprot.writeI32(struct.data.size());
                    for (Map.Entry<String, OnlineStatus> _iter55 : struct.data.entrySet()) {
                        oprot.writeString(_iter55.getKey());
                        _iter55.getValue().write(oprot);
                    }
                }
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, OnlineCheck struct) throws org.apache.thrift.TException {
            TTupleProtocol iprot = (TTupleProtocol) prot;
            BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.rid = iprot.readI32();
                struct.setRidIsSet(true);
            }
            if (incoming.get(1)) {
                {
                    org.apache.thrift.protocol.TMap _map56 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
                    struct.data = new HashMap<String, OnlineStatus>(2 * _map56.size);
                    for (int _i57 = 0; _i57 < _map56.size; ++_i57) {
                        String _key58; // required
                        OnlineStatus _val59; // required
                        _key58 = iprot.readString();
                        _val59 = new OnlineStatus();
                        _val59.read(iprot);
                        struct.data.put(_key58, _val59);
                    }
                }
                struct.setDataIsSet(true);
            }
        }
    }

}

