package com.rh.core.icbc.pushwxtodo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="n" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FilterItem" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="OwnerSSICID" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="AppID" type="{http://webservice.iipa/}ArrayOfInt" minOccurs="0"/>
 *         &lt;element name="ToDoID" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Url" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="ExpireDate" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="CreateTime" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Action" type="{http://webservice.iipa/}ArrayOfInt" minOccurs="0"/>
 *         &lt;element name="UpdateTime" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Remark1" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Remark2" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="Remark3" type="{http://webservice.iipa/}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "n",
    "filterItem",
    "ownerSSICID",
    "appID",
    "toDoID",
    "title",
    "url",
    "expireDate",
    "createTime",
    "action",
    "updateTime",
    "remark1",
    "remark2",
    "remark3"
})
@XmlRootElement(name = "SaveToDo")
public class SaveToDo {

    protected int n;
    @XmlElement(name = "FilterItem")
    protected ArrayOfString filterItem;
    @XmlElement(name = "OwnerSSICID")
    protected ArrayOfString ownerSSICID;
    @XmlElement(name = "AppID")
    protected ArrayOfInt appID;
    @XmlElement(name = "ToDoID")
    protected ArrayOfString toDoID;
    @XmlElement(name = "Title")
    protected ArrayOfString title;
    @XmlElement(name = "Url")
    protected ArrayOfString url;
    @XmlElement(name = "ExpireDate")
    protected ArrayOfString expireDate;
    @XmlElement(name = "CreateTime")
    protected ArrayOfString createTime;
    @XmlElement(name = "Action")
    protected ArrayOfInt action;
    @XmlElement(name = "UpdateTime")
    protected ArrayOfString updateTime;
    @XmlElement(name = "Remark1")
    protected ArrayOfString remark1;
    @XmlElement(name = "Remark2")
    protected ArrayOfString remark2;
    @XmlElement(name = "Remark3")
    protected ArrayOfString remark3;

    /**
     * 获取n属性的值。
     * 
     */
    public int getN() {
        return n;
    }

    /**
     * 设置n属性的值。
     * 
     */
    public void setN(int value) {
        this.n = value;
    }

    /**
     * 获取filterItem属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getFilterItem() {
        return filterItem;
    }

    /**
     * 设置filterItem属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setFilterItem(ArrayOfString value) {
        this.filterItem = value;
    }

    /**
     * 获取ownerSSICID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getOwnerSSICID() {
        return ownerSSICID;
    }

    /**
     * 设置ownerSSICID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setOwnerSSICID(ArrayOfString value) {
        this.ownerSSICID = value;
    }

    /**
     * 获取appID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getAppID() {
        return appID;
    }

    /**
     * 设置appID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setAppID(ArrayOfInt value) {
        this.appID = value;
    }

    /**
     * 获取toDoID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getToDoID() {
        return toDoID;
    }

    /**
     * 设置toDoID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setToDoID(ArrayOfString value) {
        this.toDoID = value;
    }

    /**
     * 获取title属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getTitle() {
        return title;
    }

    /**
     * 设置title属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setTitle(ArrayOfString value) {
        this.title = value;
    }

    /**
     * 获取url属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getUrl() {
        return url;
    }

    /**
     * 设置url属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setUrl(ArrayOfString value) {
        this.url = value;
    }

    /**
     * 获取expireDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getExpireDate() {
        return expireDate;
    }

    /**
     * 设置expireDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setExpireDate(ArrayOfString value) {
        this.expireDate = value;
    }

    /**
     * 获取createTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getCreateTime() {
        return createTime;
    }

    /**
     * 设置createTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setCreateTime(ArrayOfString value) {
        this.createTime = value;
    }

    /**
     * 获取action属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getAction() {
        return action;
    }

    /**
     * 设置action属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setAction(ArrayOfInt value) {
        this.action = value;
    }

    /**
     * 获取updateTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置updateTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setUpdateTime(ArrayOfString value) {
        this.updateTime = value;
    }

    /**
     * 获取remark1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getRemark1() {
        return remark1;
    }

    /**
     * 设置remark1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setRemark1(ArrayOfString value) {
        this.remark1 = value;
    }

    /**
     * 获取remark2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getRemark2() {
        return remark2;
    }

    /**
     * 设置remark2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setRemark2(ArrayOfString value) {
        this.remark2 = value;
    }

    /**
     * 获取remark3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getRemark3() {
        return remark3;
    }

    /**
     * 设置remark3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setRemark3(ArrayOfString value) {
        this.remark3 = value;
    }

}
