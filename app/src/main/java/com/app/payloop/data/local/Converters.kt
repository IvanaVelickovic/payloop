package com.app.payloop.data.local

import androidx.room.TypeConverter
import com.app.payloop.data.model.FrequencyUnit

class Converters {

    @TypeConverter
    fun fromFrequencyUnit(value: FrequencyUnit): String{
        return value.name;
    }

    @TypeConverter
    fun toFrequencyUnit(value: String): FrequencyUnit {
        return FrequencyUnit.valueOf(value);
    }
}