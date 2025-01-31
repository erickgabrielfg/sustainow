package io.github.sustainow.repository.formulary

import io.github.sustainow.domain.model.Formulary
import io.github.sustainow.domain.model.FormularyAnswer
import kotlinx.datetime.LocalDate

/**
 * Repository to handle formulary data
 */
interface FormularyRepository {
    suspend fun getFormulary(area: String): Formulary

    /**
     * Gets the formulary answers of an user in a given time period
     */
    suspend fun getAnswered(
        area: String,
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<FormularyAnswer>

    /**
     * Adds new formulary answers made by an user
     */
    suspend fun addAnswers(
        answers: List<FormularyAnswer>,
        userId: String,
    ): List<FormularyAnswer>

    /**
     * Updates a formulary with new answers
     */
    suspend fun updateAnswers(
        answers: List<FormularyAnswer>,
        userId: String,
    ): List<FormularyAnswer>
}
