package com.gap.bis_inspection.db.enumtype;

/**
 * Created by root on 9/28/15.
 */
public enum EntityNameEn {
    Person(0), Company(1), Car(2), ViolationCode(3), Finance(4), Line(5), AllEntity(6), User(7), ViolationCodeAction(8),
    DriverProfile(9), DriverJob(10), Employee(11), LineCompany(12), LineCapacity(13), Violation(14), Licence(15), CarColor(16),
    CarProfit(17), CarUsage(18), Address(19), OrganizationChart(20), Terminal(21), LinePath(22), LinePrice(23), Station(24),
    LinePathStation(25), LinePriceStructure(26), ViolationActionType(27), ViolationBase(28), ViolationBaseType(29),
    ViolationBaseAction(30), ViolationSerial(31), Vehicle(32), AttachFile(33), AttachFileSetting(34), SystemParameter(35),
    HistoryLog(36), Committee(37), CommitteeSubject(38), Calender(39), Complaint(40), LineDailyReport(41), LineDailyReportDetail(42),
    CompanyGrading(43), CompanyGradingDetail(44), CompanyGradingItem(45), CarOption(46), SubscribersService(47), LineTerminal(48),
    Subscribers(49), BlackList(50), ComplaintBase(51), ComplaintInvestigation(52), Alert(53), Meeting(54), MeetingType(55),
    MeetingTypeEmployee(56), MeetingSubject(57), SystemProcessSetting(58), Template(59), Message(60), MessageReceiver(61),
    CarOptionParking(62), CarOptionGasStation(63), CarOptionAdvertisement(64), CarOptionPenalties(65), CarOptionActivityLicence(66),
    CompanyBranch(67), Province(68), City(69), ContractorOption(70), SchoolService(71), SchoolYear(72), Contractor(73), Work(74), WorkReceiver(75),
    LegalPerson(76), Contracts(77), ContractsDetail(78), News(79), Permission(80), PermissionGroup(81), Role(82), PermissionRole(83), UserRole(84),
    CarOptionDriverWelfareService(85), ReceivedMessage(86), GeometricNet(87), CarPlate(88), Plate(89), CarOptionCarDevice(90),
    EntityAttribute(91), EntityAttributeValue(92), Document(93), DocumentVersion(94), ProcessIndex(95), ProcessIndexValue(96), EntityAttributeParam(97),
    Menu(98), Report(99), ReportParameter(100), Script(101), LineOption(102), LinePriceDriver(103), EmployeeDetail(104), CarOptionTechnicalCheck(105),
    EntityDailyActivity(106), DailyEvent(107), Event(108), TimeLine(109), TimeLineDetail(110), CompanyGradingGroup(111), Course(112), ProcessCourse(113),
    PersonCourse(114), CompanyCourse(115), Ceremony(116), CeremonyDetail(117), CarOptionCarInsurance(118), EntityDailyActivityLine(119), ChartRole(120),
    LoginHistory(121), CarOptionSubsidyPrivateCo(122), CarOptionCarStop(123), Shift(124), EntityShift(125), SalaryAttribute(126),
    CarOptionDriverOfficialLicence(127), PersonTimeOff(128), EventOption(129), Comment(130), Incident(131), Defect(132), DefectEntity(133), DailyEventSOS(134),
    DailyEventGuard(135), EntityDailyActivityCar(136), EntityDailyActivityDriver(137), EntityDailyActivityLineCompany(138), DailyEventDetail(139), EntitiesRelated(140),
    CarDetail(141), IncidentEntity(142), CompanySchool(143), LinePriceCompany(144), IncidentExpert(145), CarDamages(146), CarDamageBase(147),
    IncidentCompensation(148), CompanySetting(149), ProcessGap(150), ProcessCar(151),LineSetFactorParam(152),LineCompanyDailyInfo(153),
    LineInvoice(154), GapFactor(155), GpsHalfPathInfo(156), GpsViolationInfo(157), EtCardData(158), EtCardDataDetail(159), DailyEventSysParam(160), WebServiceCallLog(161),
    Advertisement(162), AdvertisementDetail(163), Adverts(164), EntityVisit(165), ProcessEvent(166), CarInvoice(167), CarDailyInfo(168), CarInvoiceDetail(169),
    DocumentDevice(170), CommitteeRequest(171), CarHistoryLog(172), MainIncidentHistory(173), MainIncidentHistoryDetail(174),
    ChatGroup(175), ChatGroupMembers(176), ChatMessage(177), ChatMessageReceiver(178),GeometricNetType(179), Goods(180), GoodsModel(181), GoodsUsed(182),
    ComplaintReport(183), SalaryAttributeDetails(184), InspectAppAttachFile(185), SurveyForm(186), SurveyFormMembers(187), SurveyQuestions(188),
    SurveyFormQuestions(189), SurveyFormAnswerInfo(190), SurveyFormAnswer(191), ContractsSetting(192),SchoolServiceStudent(193),
    ProcessBisSetting(194), ProcessBisData(195), ContractInvoice(196), SubscribersComment(197), SubscribersRequest(198), SubscribersRequestCar(199),
    ContractInvoiceDetail(200),CarOptionFuelLicence(201),CarOptionFuelLicenceDetail(202), PropertyBis(203),GpsMonthlyCarReport(204), CarInvoiceAdv(205),
    EventReport(206);


    private int code;

    EntityNameEn(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public String getFullName() {
        return this.getClass().getName() + "." + this.name();
    }

    public String getSummeryName() {
        return this.getClass().getName() + "." + this.name() + ".Summery";
    }

    public static EntityNameEn valueOf(int code) {
        for (EntityNameEn entityNameEn : EntityNameEn.values()) {
            if (code == entityNameEn.getCode()) {
                return entityNameEn;
            }
        }
        return null;
    }


}
