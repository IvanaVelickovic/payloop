package com.app.payloop.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromFrequencyUnit(value: FrequencyUnit): String{
        return value.name;
    }

    @TypeConverter
    fun toFrequencyUnit(value: String): FrequencyUnit{
        return FrequencyUnit.valueOf(value);
    }
}