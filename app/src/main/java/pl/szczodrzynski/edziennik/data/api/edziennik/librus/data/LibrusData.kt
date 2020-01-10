/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-5.
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.librus.data

import pl.szczodrzynski.edziennik.R
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.*
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.data.api.*
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.data.messages.LibrusMessagesGetList
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.data.synergia.LibrusSynergiaHomework
import pl.szczodrzynski.edziennik.data.api.edziennik.librus.data.synergia.LibrusSynergiaInfo
import pl.szczodrzynski.edziennik.data.db.entity.Message
import pl.szczodrzynski.edziennik.utils.Utils

class LibrusData(val data: DataLibrus, val onSuccess: () -> Unit) {
    companion object {
        private const val TAG = "LibrusEndpoints"
    }

    init {
        nextEndpoint(onSuccess)
    }

    private fun nextEndpoint(onSuccess: () -> Unit) {
        if (data.targetEndpointIds.isEmpty()) {
            onSuccess()
            return
        }
        if (data.cancelled) {
            onSuccess()
            return
        }
        useEndpoint(data.targetEndpointIds.removeAt(0)) {
            data.progress(data.progressStep)
            nextEndpoint(onSuccess)
        }
    }

    private fun useEndpoint(endpointId: Int, onSuccess: () -> Unit) {
        Utils.d(TAG, "Using endpoint $endpointId")
        when (endpointId) {
            /**
             * API
             */
            ENDPOINT_LIBRUS_API_ME -> {
                data.startProgress(R.string.edziennik_progress_endpoint_student_info)
                LibrusApiMe(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_SCHOOLS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_school_info)
                LibrusApiSchools(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_CLASSES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_classes)
                LibrusApiClasses(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_VIRTUAL_CLASSES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_teams)
                LibrusApiVirtualClasses(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_UNITS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_units)
                LibrusApiUnits(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_USERS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_teachers)
                LibrusApiUsers(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_SUBJECTS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_subjects)
                LibrusApiSubjects(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_CLASSROOMS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_classrooms)
                LibrusApiClassrooms(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_LESSONS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_lessons)
                LibrusApiLessons(data, onSuccess)
            }
            // TODO push config
            ENDPOINT_LIBRUS_API_TIMETABLES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_timetable)
                LibrusApiTimetables(data, onSuccess)
            }

            ENDPOINT_LIBRUS_API_NORMAL_GRADE_CATEGORIES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_categories)
                LibrusApiGradeCategories(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_BEHAVIOUR_GRADE_CATEGORIES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_categories)
                LibrusApiBehaviourGradeCategories(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_DESCRIPTIVE_GRADE_CATEGORIES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_categories)
                LibrusApiDescriptiveGradeCategories(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_TEXT_GRADE_CATEGORIES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_categories)
                LibrusApiTextGradeCategories(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_POINT_GRADE_CATEGORIES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_categories)
                LibrusApiPointGradeCategories(data, onSuccess)
            }

            ENDPOINT_LIBRUS_API_NORMAL_GRADE_COMMENTS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_comments)
                LibrusApiGradeComments(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_BEHAVIOUR_GRADE_COMMENTS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grade_comments)
                LibrusApiBehaviourGradeComments(data, onSuccess)
            }

            ENDPOINT_LIBRUS_API_NORMAL_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grades)
                LibrusApiGrades(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_BEHAVIOUR_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_behaviour_grades)
                LibrusApiBehaviourGrades(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_DESCRIPTIVE_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_descriptive_grades)
                LibrusApiDescriptiveGrades(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_TEXT_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_descriptive_grades)
                LibrusApiTextGrades(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_POINT_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_point_grades)
                LibrusApiPointGrades(data, onSuccess)
            }

            ENDPOINT_LIBRUS_API_EVENT_TYPES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_event_types)
                LibrusApiEventTypes(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_EVENTS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_events)
                LibrusApiEvents(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_HOMEWORK -> {
                data.startProgress(R.string.edziennik_progress_endpoint_homework)
                LibrusApiHomework(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_LUCKY_NUMBER -> {
                data.startProgress(R.string.edziennik_progress_endpoint_lucky_number)
                LibrusApiLuckyNumber(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_NOTICE_TYPES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_notice_types)
                LibrusApiNoticeTypes(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_NOTICES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_notices)
                LibrusApiNotices(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_ATTENDANCE_TYPES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_attendance_types)
                LibrusApiAttendanceTypes(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_ATTENDANCES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_attendance)
                LibrusApiAttendances(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_ANNOUNCEMENTS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_announcements)
                LibrusApiAnnouncements(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_PT_MEETINGS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_pt_meetings)
                LibrusApiPtMeetings(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_TEACHER_FREE_DAY_TYPES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_teacher_free_day_types)
                LibrusApiTeacherFreeDayTypes(data, onSuccess)
            }
            ENDPOINT_LIBRUS_API_TEACHER_FREE_DAYS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_teacher_free_days)
                LibrusApiTeacherFreeDays(data, onSuccess)
            }

            /**
             * SYNERGIA
             */
            ENDPOINT_LIBRUS_SYNERGIA_HOMEWORK -> {
                data.startProgress(R.string.edziennik_progress_endpoint_homework)
                LibrusSynergiaHomework(data, onSuccess)
            }
            ENDPOINT_LIBRUS_SYNERGIA_INFO -> {
                data.startProgress(R.string.edziennik_progress_endpoint_student_info)
                LibrusSynergiaInfo(data, onSuccess)
            }

            /**
             * MESSAGES
             */
            ENDPOINT_LIBRUS_MESSAGES_RECEIVED -> {
                data.startProgress(R.string.edziennik_progress_endpoint_messages_inbox)
                LibrusMessagesGetList(data, type = Message.TYPE_RECEIVED, onSuccess = onSuccess)
            }
            ENDPOINT_LIBRUS_MESSAGES_SENT -> {
                data.startProgress(R.string.edziennik_progress_endpoint_messages_outbox)
                LibrusMessagesGetList(data, type = Message.TYPE_SENT, onSuccess = onSuccess)
            }

            else -> onSuccess()
        }
    }
}