package com.fastcampus.issueservice.domain.enums

enum class IssueStatus {

    TODO , IN_PROGRESS, RESOLVED;
    companion object{
        operator fun invoke(status:String) = IssueType.valueOf(status.uppercase())
    }
}
