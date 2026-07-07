package me.rerere.rikkahub.data.ai.tools.local

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.provider.AlarmClock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart

fun buildCalendarDeleteTool(context: Context): Tool = Tool(
    name = "calendar_delete",
    description = "Delete a calendar event by event ID. Requires calendar write permission.",
    parameters = {
        InputSchema.Obj(properties = buildJsonObject {
            put("event_id", buildJsonObject {
                put("type", "integer")
                put("description", "The ID of the calendar event to delete")
            })
        }, required = listOf("event_id"))
    },
    execute = { args ->
        val eventId = args.jsonObject["event_id"]?.jsonPrimitive?.contentOrNull?.toLongOrNull()
            ?: return@Tool listOf(UIMessagePart.Text(buildJsonObject {
                put("error", "INVALID_ID"); put("message", "Valid event_id is required.")
            }.toString()))
        val deleted = context.contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId), null, null)
        listOf(UIMessagePart.Text(buildJsonObject {
            put("success", deleted > 0); put("deleted_count", deleted)
        }.toString()))
    }
)

fun buildCalendarUpdateTool(context: Context): Tool = Tool(
    name = "calendar_update",
    description = "Update a calendar event's title/description by event ID.",
    parameters = {
        InputSchema.Obj(properties = buildJsonObject {
            put("event_id", buildJsonObject { put("type", "integer"); put("description", "Event ID") })
            put("title", buildJsonObject { put("type", "string"); put("description", "New title (optional)") })
            put("description", buildJsonObject { put("type", "string"); put("description", "New description (optional)") })
        }, required = listOf("event_id"))
    },
    execute = { args ->
        val eventId = args.jsonObject["event_id"]?.jsonPrimitive?.contentOrNull?.toLongOrNull()
            ?: return@Tool listOf(UIMessagePart.Text("{\"error\":\"INVALID_ID\"}"))
        val values = ContentValues()
        args.jsonObject["title"]?.jsonPrimitive?.contentOrNull?.let { values.put(CalendarContract.Events.TITLE, it) }
        args.jsonObject["description"]?.jsonPrimitive?.contentOrNull?.let { values.put(CalendarContract.Events.DESCRIPTION, it) }
        val updated = context.contentResolver.update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId), values, null, null)
        listOf(UIMessagePart.Text(buildJsonObject { put("success", updated > 0) }.toString()))
    }
)

fun buildSetAlarmTool(context: Context): Tool = Tool(
    name = "set_alarm",
    description = "Set a one-time alarm on the device.",
    parameters = {
        InputSchema.Obj(properties = buildJsonObject {
            put("hour", buildJsonObject { put("type", "integer"); put("description", "Hour (0-23)") })
            put("minute", buildJsonObject { put("type", "integer"); put("description", "Minute (0-59)") })
            put("message", buildJsonObject { put("type", "string"); put("description", "Alarm label") })
        }, required = listOf("hour", "minute"))
    },
    needsApproval = { true },
    execute = { args ->
        val hour = args.jsonObject["hour"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 8
        val minute = args.jsonObject["minute"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
        val msg = args.jsonObject["message"]?.jsonPrimitive?.contentOrNull ?: "Alarm"
        context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour); putExtra(AlarmClock.EXTRA_MINUTE, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, msg); flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
        listOf(UIMessagePart.Text(buildJsonObject {
            put("success", true); put("hour", hour); put("minute", minute)
        }.toString()))
    }
)
