// =========================================================================
// Copyright 2021 T-Mobile, US
// 
// Licensed under the Apache License, Version 2.0 (the "License")
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// See the readme.txt file for additional language around disclaimer of warranties.
// =========================================================================

package com.tmobile.cso.vault.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TMOMetaData implements Serializable {


	private static final long serialVersionUID = -5203897039537250922L;
	private String aliasTag;
	private String applicationTag;
	private String applicationGuid;
	private String comment;
	private String createdTs;
	private String  createdBy;
	private String modifiedTs;
	private String modifiedBy;
	private String active;
	private String isActualTag;
	List<String> internalCertificateList;
	List<String> externalCertificateList;
	private String applicationName;
	private String applicationOwnerEmailId;
	private String projectLeadEmailId;
	private boolean updateFlag;

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationOwnerEmailId() {
		return applicationOwnerEmailId;
	}

	public void setApplicationOwnerEmailId(String applicationOwnerEmailId) {
		this.applicationOwnerEmailId = applicationOwnerEmailId;
	}

	public String getProjectLeadEmailId() {
		return projectLeadEmailId;
	}

	public void setProjectLeadEmailId(String projectLeadEmailId) {
		this.projectLeadEmailId = projectLeadEmailId;
	}

	public TMOMetaData() {
		super();
	}

	public TMOMetaData(String aliasTag, String applicationTag, String applicationGuid,
								 String comment, String createdTs, List<String> internalCertificateList,
								 List<String> externalCertificateList, String createdBy, String modifiedTs, String modifiedBy, String active, String isActualTag) {
		this.aliasTag = aliasTag;
		this.applicationTag = applicationTag;
		this.applicationGuid = applicationGuid;
		this.comment = comment;
		this.createdTs = createdTs;
		this.internalCertificateList = internalCertificateList;
		this.externalCertificateList = externalCertificateList;
		this.createdBy = createdBy;
		this.modifiedTs = modifiedTs;
		this.modifiedBy = modifiedBy;
		this.active = active;
		this.isActualTag = isActualTag;
	}

	

	

	

	public String getIsActualTag() {
		return isActualTag;
	}

	public void setIsActualTag(String isActualTag) {
		this.isActualTag = isActualTag;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedTs() {
		return modifiedTs;
	}

	public void setModifiedTs(String modifiedTs) {
		this.modifiedTs = modifiedTs;
	}

	public String getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(String createdTs) {
		this.createdTs = createdTs;
	}

	public String getAliasTag() {
		return aliasTag;
	}

	public void setAliasTag(String aliasTag) {
		this.aliasTag = aliasTag;
	}

	

	

	public String getApplicationTag() {
		return applicationTag;
	}

	public void setApplicationTag(String applicationTag) {
		this.applicationTag = applicationTag;
	}

	

	

	public String getApplicationGuid() {
		return applicationGuid;
	}

	public void setApplicationGuid(String applicationGuid) {
		this.applicationGuid = applicationGuid;
	}

	

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getInternalCertificateList() {
		return internalCertificateList;
	}

	public void setInternalCertificateList(List<String> internalCertificateList) {
		this.internalCertificateList = internalCertificateList;
	}

	public List<String> getExternalCertificateList() {
		return externalCertificateList;
	}

	public void setExternalCertificateList(List<String> externalCertificateList) {
		this.externalCertificateList = externalCertificateList;
	}

	

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "TMOMetaData{" +
				"aliasTag='" + aliasTag + '\'' +
				", applicationTag='" + applicationTag + '\'' +
				", applicationGuid='" + applicationGuid + '\'' +
				", comment='" + comment + '\'' + 
				", createdTs='" + createdTs + '\'' +
				", modifiedTs='" + modifiedTs + '\'' +
				", createdBy=" + createdBy + 
				", modifiedBy=" + modifiedBy + 
				", active=" + active + 
				", isActualTag=" + isActualTag +
				", internalCertificateList=" + internalCertificateList +
				", externalCertificateList=" + externalCertificateList +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TMOMetaData that = (TMOMetaData) o;
		return createdBy.equals(that.createdBy) &&
				aliasTag.equals(that.aliasTag) &&
				applicationTag.equals(that.applicationTag) &&
				applicationGuid.equals(that.applicationGuid) &&
				comment.equals(that.comment) && 
				createdTs.equals(that.createdTs) &&
				modifiedTs.equals(modifiedTs) && 
				modifiedBy.equals(modifiedBy) && 
				active.equals(active) && 
				isActualTag.equals(isActualTag) &&
				internalCertificateList.equals(that.internalCertificateList) &&
				externalCertificateList.equals(that.externalCertificateList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aliasTag, applicationTag, applicationGuid, comment, createdTs, createdBy, modifiedTs, modifiedBy, active, isActualTag, internalCertificateList, externalCertificateList);
	}
}
