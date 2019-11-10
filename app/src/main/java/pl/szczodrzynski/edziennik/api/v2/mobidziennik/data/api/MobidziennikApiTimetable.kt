/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-11.
 */

package pl.szczodrzynski.edziennik.api.v2.mobidziennik.data.api

import pl.szczodrzynski.edziennik.api.v2.mobidziennik.DataMobidziennik
import pl.szczodrzynski.edziennik.data.db.modules.metadata.Metadata
import pl.szczodrzynski.edziennik.data.db.modules.timetable.Lesson
import pl.szczodrzynski.edziennik.fixName
import pl.szczodrzynski.edziennik.singleOrNull
import pl.szczodrzynski.edziennik.utils.models.Date
import pl.szczodrzynski.edziennik.utils.models.Time

class MobidziennikApiTimetable(val data: DataMobidziennik, rows: List<String>) {
    init {
        val lessons = rows.filterNot { it.isEmpty() }.map { it.split("|") }

        for (lesson in lessons) {
            val date = Date.fromYmd(lesson[2])
            val startTime = Time.fromYmdHm(lesson[3])
            val endTime = Time.fromYmdHm(lesson[4])
            val id = date.combineWith(startTime) / 1000L

            val subjectId = data.subjectList.singleOrNull { it.longName == lesson[5] }?.id ?: -1
            val teacherId = data.teacherList.singleOrNull { it.fullNameLastFirst == (lesson[7]+" "+lesson[6]).fixName() }?.id ?: -1
            val teamId = data.teamList.singleOrNull { it.name == lesson[8]+lesson[9] }?.id ?: -1
            val classroom = lesson[11]

            Lesson(data.profileId, id).also {
                when (lesson[1]) {
                    "plan_lekcji", "lekcja" -> {
                        it.type = Lesson.TYPE_NORMAL
                        it.date = date
                        it.startTime = startTime
                        it.endTime = endTime
                        it.subjectId = subjectId
                        it.teacherId = teacherId
                        it.teamId = teamId
                        it.classroom = classroom
                    }
                    "lekcja_odwolana" -> {
                        it.type = Lesson.TYPE_CANCELLED
                        it.date = date
                        it.startTime = startTime
                        it.endTime = endTime
                        it.oldSubjectId = subjectId
                        //it.oldTeacherId = teacherId
                        it.oldTeamId = teamId
                        //it.oldClassroom = classroom
                    }
                    "zastepstwo" -> {
                        it.type = Lesson.TYPE_CHANGE
                        it.date = date
                        it.startTime = startTime
                        it.endTime = endTime
                        it.subjectId = subjectId
                        it.teacherId = teacherId
                        it.teamId = teamId
                        it.classroom = classroom
                    }
                }

                if (it.type != Lesson.TYPE_NORMAL) {
                    data.metadataList.add(
                            Metadata(
                                    data.profileId,
                                    Metadata.TYPE_LESSON_CHANGE,
                                    it.id,
                                    data.profile?.empty ?: false,
                                    data.profile?.empty ?: false,
                                    System.currentTimeMillis()
                            ))
                }
                data.lessonNewList += it
            }
        }
        /*for (lessonStr in rows) {
            if (lessonStr.isNotEmpty()) {
                val lesson = lessonStr.split("|")

                if (lesson[0].toInt() != data.studentId)
                    continue

                if (lesson[1] == "plan_lekcji" || lesson[1] == "lekcja") {
                    val lessonObject = Lesson(data.profileId, lesson[2], lesson[3], lesson[4])

                    data.subjectList.singleOrNull { it.longName == lesson[5] }?.let {
                        lessonObject.subjectId = it.id
                    }
                    data.teacherList.singleOrNull { it.fullNameLastFirst == (lesson[7]+" "+lesson[6]).fixName() }?.let {
                        lessonObject.teacherId = it.id
                    }
                    data.teamList.singleOrNull { it.name == lesson[8]+lesson[9] }?.let {
                        lessonObject.teamId = it.id
                    }
                    lessonObject.classroomName = lesson[11]
                    data.lessonList.add(lessonObject)
                }
            }
        }

        // searching for all changes
        for (lessonStr in rows) {
            if (lessonStr.isNotEmpty()) {
                val lesson = lessonStr.split("|")

                if (lesson[0].toInt() != data.studentId)
                    continue

                if (lesson[1] == "zastepstwo" || lesson[1] == "lekcja_odwolana") {
                    val lessonChange = LessonChange(data.profileId, lesson[2], lesson[3], lesson[4])

                    data.subjectList.singleOrNull { it.longName == lesson[5] }?.let {
                        lessonChange.subjectId = it.id
                    }
                    data.teacherList.singleOrNull { it.fullNameLastFirst == (lesson[7]+" "+lesson[6]).fixName() }?.let {
                        lessonChange.teacherId = it.id
                    }
                    data.teamList.singleOrNull { it.name == lesson[8]+lesson[9] }?.let {
                        lessonChange.teamId = it.id
                    }

                    if (lesson[1] == "zastepstwo") {
                        lessonChange.type = LessonChange.TYPE_CHANGE
                    }
                    else if (lesson[1] == "lekcja_odwolana") {
                        lessonChange.type = LessonChange.TYPE_CANCELLED
                    }
                    else if (lesson[1] == "lekcja") {
                        lessonChange.type = LessonChange.TYPE_ADDED
                    }
                    lessonChange.classroomName = lesson[11]

                    val originalLesson = lessonChange.getOriginalLesson(data.lessonList)

                    if (lessonChange.type == LessonChange.TYPE_ADDED) {
                        if (originalLesson == null) {
                            // original lesson doesn't exist, save a new addition
                            // TODO
                            *//*if (!RegisterLessonChange.existsAddition(app.profile, registerLessonChange)) {
                            app.profile.timetable.addLessonAddition(registerLessonChange);
                        }*//*
                        } else {
                            // original lesson exists, so we need to compare them
                            if (!lessonChange.matches(originalLesson)) {
                                // the lessons are different, so it's probably a lesson change
                                // ahhh this damn API
                                lessonChange.type = LessonChange.TYPE_CHANGE
                            }
                        }

                    }
                    if (lessonChange.type != LessonChange.TYPE_ADDED) {
                        // it's not a lesson addition
                        data.lessonChangeList.add(lessonChange)
                        data.metadataList.add(
                                Metadata(
                                        data.profileId,
                                        Metadata.TYPE_LESSON_CHANGE,
                                        lessonChange.id,
                                        data.profile?.empty ?: false,
                                        data.profile?.empty ?: false,
                                        System.currentTimeMillis()
                                ))
                        if (originalLesson == null) {
                            // there is no original lesson, so we have to add one in order to change it
                            data.lessonList.add(Lesson.fromLessonChange(lessonChange))
                        }
                    }
                }
            }
        }*/
    }
}