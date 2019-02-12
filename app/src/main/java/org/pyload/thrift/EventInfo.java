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

public class EventInfo implements org.apache.thrift.TBase<EventInfo, EventInfo._Fields>, java.io.Serializable, Cloneable {
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EventInfo");
    private static final org.apache.thrift.protocol.TField EVENTNAME_FIELD_DESC = new org.apache.thrift.protocol.TField("eventname", org.apache.thrift.protocol.TType.STRING, (short) 1);
    private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I32, (short) 2);
    private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short) 3);
    private static final org.apache.thrift.protocol.TField DESTINATION_FIELD_DESC = new org.apache.thrift.protocol.TField("destination", org.apache.thrift.protocol.TType.I32, (short) 4);
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    // isset id assignments
    private static final int __ID_ISSET_ID = 0;

    static {
        schemes.put(StandardScheme.class, new EventInfoStandardSchemeFactory());
        schemes.put(TupleScheme.class, new EventInfoTupleSchemeFactory());
    }

    static {
        Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.EVENTNAME, new org.apache.thrift.meta_data.FieldMetaData("eventname", org.apache.thrift.TFieldRequirementType.DEFAULT,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
        tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
        tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, ElementType.class)));
        tmpMap.put(_Fields.DESTINATION, new org.apache.thrift.meta_data.FieldMetaData("destination", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, Destination.class)));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EventInfo.class, metaDataMap);
    }

    public String eventname; // required
    public int id; // required
    /**
     * @see ElementType
     */
    public ElementType type; // required
    /**
     * @see Destination
     */
    public Destination destination; // required
    private BitSet __isset_bit_vector = new BitSet(1);
    private _Fields optionals[] = {_Fields.ID, _Fields.TYPE, _Fields.DESTINATION};

    public EventInfo() {
    }

    public EventInfo(
            String eventname) {
        this();
        this.eventname = eventname;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public EventInfo(EventInfo other) {
        __isset_bit_vector.clear();
        __isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetEventname()) {
            this.eventname = other.eventname;
        }
        this.id = other.id;
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetDestination()) {
            this.destination = other.destination;
        }
    }

    public EventInfo deepCopy() {
        return new EventInfo(this);
    }

    @Override
    public void clear() {
        this.eventname = null;
        setIdIsSet(false);
        this.id = 0;
        this.type = null;
        this.destination = null;
    }

    public String getEventname() {
        return this.eventname;
    }

    public EventInfo setEventname(String eventname) {
        this.eventname = eventname;
        return this;
    }

    public void unsetEventname() {
        this.eventname = null;
    }

    /**
     * Returns true if field eventname is set (has been assigned a value) and false otherwise
     */
    public boolean isSetEventname() {
        return this.eventname != null;
    }

    public void setEventnameIsSet(boolean value) {
        if (!value) {
            this.eventname = null;
        }
    }

    public int getId() {
        return this.id;
    }

    public EventInfo setId(int id) {
        this.id = id;
        setIdIsSet(true);
        return this;
    }

    public void unsetId() {
        __isset_bit_vector.clear(__ID_ISSET_ID);
    }

    /**
     * Returns true if field id is set (has been assigned a value) and false otherwise
     */
    public boolean isSetId() {
        return __isset_bit_vector.get(__ID_ISSET_ID);
    }

    public void setIdIsSet(boolean value) {
        __isset_bit_vector.set(__ID_ISSET_ID, value);
    }

    /**
     * @see ElementType
     */
    public ElementType getType() {
        return this.type;
    }

    /**
     * @see ElementType
     */
    public EventInfo setType(ElementType type) {
        this.type = type;
        return this;
    }

    public void unsetType() {
        this.type = null;
    }

    /**
     * Returns true if field type is set (has been assigned a value) and false otherwise
     */
    public boolean isSetType() {
        return this.type != null;
    }

    public void setTypeIsSet(boolean value) {
        if (!value) {
            this.type = null;
        }
    }

    /**
     * @see Destination
     */
    public Destination getDestination() {
        return this.destination;
    }

    /**
     * @see Destination
     */
    public EventInfo setDestination(Destination destination) {
        this.destination = destination;
        return this;
    }

    public void unsetDestination() {
        this.destination = null;
    }

    /**
     * Returns true if field destination is set (has been assigned a value) and false otherwise
     */
    public boolean isSetDestination() {
        return this.destination != null;
    }

    public void setDestinationIsSet(boolean value) {
        if (!value) {
            this.destination = null;
        }
    }

    public void setFieldValue(_Fields field, Object value) {
        switch (field) {
            case EVENTNAME:
                if (value == null) {
                    unsetEventname();
                } else {
                    setEventname((String) value);
                }
                break;

            case ID:
                if (value == null) {
                    unsetId();
                } else {
                    setId((Integer) value);
                }
                break;

            case TYPE:
                if (value == null) {
                    unsetType();
                } else {
                    setType((ElementType) value);
                }
                break;

            case DESTINATION:
                if (value == null) {
                    unsetDestination();
                } else {
                    setDestination((Destination) value);
                }
                break;

        }
    }

    public Object getFieldValue(_Fields field) {
        switch (field) {
            case EVENTNAME:
                return getEventname();

            case ID:
                return Integer.valueOf(getId());

            case TYPE:
                return getType();

            case DESTINATION:
                return getDestination();

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
            case EVENTNAME:
                return isSetEventname();
            case ID:
                return isSetId();
            case TYPE:
                return isSetType();
            case DESTINATION:
                return isSetDestination();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof EventInfo)
            return this.equals((EventInfo) that);
        return false;
    }

    public boolean equals(EventInfo that) {
        if (that == null)
            return false;

        boolean this_present_eventname = true && this.isSetEventname();
        boolean that_present_eventname = true && that.isSetEventname();
        if (this_present_eventname || that_present_eventname) {
            if (!(this_present_eventname && that_present_eventname))
                return false;
            if (!this.eventname.equals(that.eventname))
                return false;
        }

        boolean this_present_id = true && this.isSetId();
        boolean that_present_id = true && that.isSetId();
        if (this_present_id || that_present_id) {
            if (!(this_present_id && that_present_id))
                return false;
            if (this.id != that.id)
                return false;
        }

        boolean this_present_type = true && this.isSetType();
        boolean that_present_type = true && that.isSetType();
        if (this_present_type || that_present_type) {
            if (!(this_present_type && that_present_type))
                return false;
            if (!this.type.equals(that.type))
                return false;
        }

        boolean this_present_destination = true && this.isSetDestination();
        boolean that_present_destination = true && that.isSetDestination();
        if (this_present_destination || that_present_destination) {
            if (!(this_present_destination && that_present_destination))
                return false;
            return this.destination.equals(that.destination);
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public int compareTo(EventInfo other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;
        EventInfo typedOther = other;

        lastComparison = Boolean.valueOf(isSetEventname()).compareTo(typedOther.isSetEventname());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetEventname()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventname, typedOther.eventname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetId()).compareTo(typedOther.isSetId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetId()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, typedOther.id);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetType()).compareTo(typedOther.isSetType());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetType()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetDestination()).compareTo(typedOther.isSetDestination());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetDestination()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.destination, typedOther.destination);
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
        StringBuilder sb = new StringBuilder("EventInfo(");
        boolean first = true;

        sb.append("eventname:");
        if (this.eventname == null) {
            sb.append("null");
        } else {
            sb.append(this.eventname);
        }
        first = false;
        if (isSetId()) {
            if (!first) sb.append(", ");
            sb.append("id:");
            sb.append(this.id);
            first = false;
        }
        if (isSetType()) {
            if (!first) sb.append(", ");
            sb.append("type:");
            if (this.type == null) {
                sb.append("null");
            } else {
                sb.append(this.type);
            }
            first = false;
        }
        if (isSetDestination()) {
            if (!first) sb.append(", ");
            sb.append("destination:");
            if (this.destination == null) {
                sb.append("null");
            } else {
                sb.append(this.destination);
            }
            first = false;
        }
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
        EVENTNAME((short) 1, "eventname"),
        ID((short) 2, "id"),
        /**
         * @see ElementType
         */
        TYPE((short) 3, "type"),
        /**
         * @see Destination
         */
        DESTINATION((short) 4, "destination");

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
                case 1: // EVENTNAME
                    return EVENTNAME;
                case 2: // ID
                    return ID;
                case 3: // TYPE
                    return TYPE;
                case 4: // DESTINATION
                    return DESTINATION;
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

    private static class EventInfoStandardSchemeFactory implements SchemeFactory {
        public EventInfoStandardScheme getScheme() {
            return new EventInfoStandardScheme();
        }
    }

    private static class EventInfoStandardScheme extends StandardScheme<EventInfo> {

        public void read(org.apache.thrift.protocol.TProtocol iprot, EventInfo struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // EVENTNAME
                        if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                            struct.eventname = iprot.readString();
                            struct.setEventnameIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 2: // ID
                        if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                            struct.id = iprot.readI32();
                            struct.setIdIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 3: // TYPE
                        if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                            struct.type = ElementType.findByValue(iprot.readI32());
                            struct.setTypeIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 4: // DESTINATION
                        if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                            struct.destination = Destination.findByValue(iprot.readI32());
                            struct.setDestinationIsSet(true);
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

        public void write(org.apache.thrift.protocol.TProtocol oprot, EventInfo struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.eventname != null) {
                oprot.writeFieldBegin(EVENTNAME_FIELD_DESC);
                oprot.writeString(struct.eventname);
                oprot.writeFieldEnd();
            }
            if (struct.isSetId()) {
                oprot.writeFieldBegin(ID_FIELD_DESC);
                oprot.writeI32(struct.id);
                oprot.writeFieldEnd();
            }
            if (struct.type != null) {
                if (struct.isSetType()) {
                    oprot.writeFieldBegin(TYPE_FIELD_DESC);
                    oprot.writeI32(struct.type.getValue());
                    oprot.writeFieldEnd();
                }
            }
            if (struct.destination != null) {
                if (struct.isSetDestination()) {
                    oprot.writeFieldBegin(DESTINATION_FIELD_DESC);
                    oprot.writeI32(struct.destination.getValue());
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class EventInfoTupleSchemeFactory implements SchemeFactory {
        public EventInfoTupleScheme getScheme() {
            return new EventInfoTupleScheme();
        }
    }

    private static class EventInfoTupleScheme extends TupleScheme<EventInfo> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, EventInfo struct) throws org.apache.thrift.TException {
            TTupleProtocol oprot = (TTupleProtocol) prot;
            BitSet optionals = new BitSet();
            if (struct.isSetEventname()) {
                optionals.set(0);
            }
            if (struct.isSetId()) {
                optionals.set(1);
            }
            if (struct.isSetType()) {
                optionals.set(2);
            }
            if (struct.isSetDestination()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetEventname()) {
                oprot.writeString(struct.eventname);
            }
            if (struct.isSetId()) {
                oprot.writeI32(struct.id);
            }
            if (struct.isSetType()) {
                oprot.writeI32(struct.type.getValue());
            }
            if (struct.isSetDestination()) {
                oprot.writeI32(struct.destination.getValue());
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, EventInfo struct) throws org.apache.thrift.TException {
            TTupleProtocol iprot = (TTupleProtocol) prot;
            BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.eventname = iprot.readString();
                struct.setEventnameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.id = iprot.readI32();
                struct.setIdIsSet(true);
            }
            if (incoming.get(2)) {
                struct.type = ElementType.findByValue(iprot.readI32());
                struct.setTypeIsSet(true);
            }
            if (incoming.get(3)) {
                struct.destination = Destination.findByValue(iprot.readI32());
                struct.setDestinationIsSet(true);
            }
        }
    }

}

