/**
 * Autogenerated by Thrift Compiler (0.8.0-dev)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package org.pyload.thrift;


public enum ElementType implements org.apache.thrift.TEnum {
    Package(0),
    File(1);

    private final int value;

    ElementType(int value) {
        this.value = value;
    }

    /**
     * Find a the enum type by its integer value, as defined in the Thrift IDL.
     *
     * @return null if the value is not found.
     */
    public static ElementType findByValue(int value) {
        switch (value) {
            case 0:
                return Package;
            case 1:
                return File;
            default:
                return null;
        }
    }

    /**
     * Get the integer value of this enum value, as defined in the Thrift IDL.
     */
    public int getValue() {
        return value;
    }
}
