
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gov.treas.fms.services.tcsonline_3_0 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TCSServiceFault_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "TCSServiceFault");
    private final static QName _StartOnlineCollection_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "startOnlineCollection");
    private final static QName _StartOnlineCollectionResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "startOnlineCollectionResponse");
    private final static QName _CompleteOnlineCollection_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "completeOnlineCollection");
    private final static QName _CompleteOnlineCollectionResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "completeOnlineCollectionResponse");
    private final static QName _CompleteOnlineCollectionWithDetails_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "completeOnlineCollectionWithDetails");
    private final static QName _CompleteOnlineCollectionWithDetailsResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "completeOnlineCollectionWithDetailsResponse");
    private final static QName _CreateForce_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "createForce");
    private final static QName _CreateForceResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "createForceResponse");
    private final static QName _CreateForceWithDetails_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "createForceWithDetails");
    private final static QName _CreateForceWithDetailsResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "createForceWithDetailsResponse");
    private final static QName _GetDetails_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "getDetails");
    private final static QName _GetDetailsResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "getDetailsResponse");
    private final static QName _CloseAuthorization_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "closeAuthorization");
    private final static QName _CloseAuthorizationResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "closeAuthorizationResponse");
    private final static QName _GetPaymentTypes_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "getPaymentTypes");
    private final static QName _GetPaymentTypesResponse_QNAME = new QName("http://fms.treas.gov/services/tcsonline_3_0", "getPaymentTypesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.treas.fms.services.tcsonline_3_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TCSServiceFault }
     * 
     */
    public TCSServiceFault createTCSServiceFault() {
        return new TCSServiceFault();
    }

    /**
     * Create an instance of {@link StartOnlineCollection }
     * 
     */
    public StartOnlineCollection createStartOnlineCollection() {
        return new StartOnlineCollection();
    }

    /**
     * Create an instance of {@link StartOnlineCollectionResponseType }
     * 
     */
    public StartOnlineCollectionResponseType createStartOnlineCollectionResponseType() {
        return new StartOnlineCollectionResponseType();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollection }
     * 
     */
    public CompleteOnlineCollection createCompleteOnlineCollection() {
        return new CompleteOnlineCollection();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionResponseType }
     * 
     */
    public CompleteOnlineCollectionResponseType createCompleteOnlineCollectionResponseType() {
        return new CompleteOnlineCollectionResponseType();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionWithDetails }
     * 
     */
    public CompleteOnlineCollectionWithDetails createCompleteOnlineCollectionWithDetails() {
        return new CompleteOnlineCollectionWithDetails();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionWithDetailsResponseType }
     * 
     */
    public CompleteOnlineCollectionWithDetailsResponseType createCompleteOnlineCollectionWithDetailsResponseType() {
        return new CompleteOnlineCollectionWithDetailsResponseType();
    }

    /**
     * Create an instance of {@link CreateForce }
     * 
     */
    public CreateForce createCreateForce() {
        return new CreateForce();
    }

    /**
     * Create an instance of {@link CreateForceResponseType }
     * 
     */
    public CreateForceResponseType createCreateForceResponseType() {
        return new CreateForceResponseType();
    }

    /**
     * Create an instance of {@link CreateForceWithDetails }
     * 
     */
    public CreateForceWithDetails createCreateForceWithDetails() {
        return new CreateForceWithDetails();
    }

    /**
     * Create an instance of {@link CreateForceWithDetailsResponseType }
     * 
     */
    public CreateForceWithDetailsResponseType createCreateForceWithDetailsResponseType() {
        return new CreateForceWithDetailsResponseType();
    }

    /**
     * Create an instance of {@link GetDetails }
     * 
     */
    public GetDetails createGetDetails() {
        return new GetDetails();
    }

    /**
     * Create an instance of {@link GetDetailsResponseType }
     * 
     */
    public GetDetailsResponseType createGetDetailsResponseType() {
        return new GetDetailsResponseType();
    }

    /**
     * Create an instance of {@link CloseAuthorization }
     * 
     */
    public CloseAuthorization createCloseAuthorization() {
        return new CloseAuthorization();
    }

    /**
     * Create an instance of {@link CloseAuthorizationResponseType }
     * 
     */
    public CloseAuthorizationResponseType createCloseAuthorizationResponseType() {
        return new CloseAuthorizationResponseType();
    }

    /**
     * Create an instance of {@link GetPaymentTypes }
     * 
     */
    public GetPaymentTypes createGetPaymentTypes() {
        return new GetPaymentTypes();
    }

    /**
     * Create an instance of {@link GetPaymentTypesResponseType }
     * 
     */
    public GetPaymentTypesResponseType createGetPaymentTypesResponseType() {
        return new GetPaymentTypesResponseType();
    }

    /**
     * Create an instance of {@link StartOnlineCollectionRequest }
     * 
     */
    public StartOnlineCollectionRequest createStartOnlineCollectionRequest() {
        return new StartOnlineCollectionRequest();
    }

    /**
     * Create an instance of {@link StartOnlineCollectionResponse }
     * 
     */
    public StartOnlineCollectionResponse createStartOnlineCollectionResponse() {
        return new StartOnlineCollectionResponse();
    }

    /**
     * Create an instance of {@link CustomCollectionFields }
     * 
     */
    public CustomCollectionFields createCustomCollectionFields() {
        return new CustomCollectionFields();
    }

    /**
     * Create an instance of {@link Classification }
     * 
     */
    public Classification createClassification() {
        return new Classification();
    }

    /**
     * Create an instance of {@link ClassificationData }
     * 
     */
    public ClassificationData createClassificationData() {
        return new ClassificationData();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionRequest }
     * 
     */
    public CompleteOnlineCollectionRequest createCompleteOnlineCollectionRequest() {
        return new CompleteOnlineCollectionRequest();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionResponse }
     * 
     */
    public CompleteOnlineCollectionResponse createCompleteOnlineCollectionResponse() {
        return new CompleteOnlineCollectionResponse();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionWithDetailsRequest }
     * 
     */
    public CompleteOnlineCollectionWithDetailsRequest createCompleteOnlineCollectionWithDetailsRequest() {
        return new CompleteOnlineCollectionWithDetailsRequest();
    }

    /**
     * Create an instance of {@link CompleteOnlineCollectionWithDetailsResponse }
     * 
     */
    public CompleteOnlineCollectionWithDetailsResponse createCompleteOnlineCollectionWithDetailsResponse() {
        return new CompleteOnlineCollectionWithDetailsResponse();
    }

    /**
     * Create an instance of {@link CreateForceRequest }
     * 
     */
    public CreateForceRequest createCreateForceRequest() {
        return new CreateForceRequest();
    }

    /**
     * Create an instance of {@link CreateForceResponse }
     * 
     */
    public CreateForceResponse createCreateForceResponse() {
        return new CreateForceResponse();
    }

    /**
     * Create an instance of {@link CreateForceWithDetailsRequest }
     * 
     */
    public CreateForceWithDetailsRequest createCreateForceWithDetailsRequest() {
        return new CreateForceWithDetailsRequest();
    }

    /**
     * Create an instance of {@link CreateForceWithDetailsResponse }
     * 
     */
    public CreateForceWithDetailsResponse createCreateForceWithDetailsResponse() {
        return new CreateForceWithDetailsResponse();
    }

    /**
     * Create an instance of {@link GetDetailsRequest }
     * 
     */
    public GetDetailsRequest createGetDetailsRequest() {
        return new GetDetailsRequest();
    }

    /**
     * Create an instance of {@link GetDetailsResponse }
     * 
     */
    public GetDetailsResponse createGetDetailsResponse() {
        return new GetDetailsResponse();
    }

    /**
     * Create an instance of {@link Transactions }
     * 
     */
    public Transactions createTransactions() {
        return new Transactions();
    }

    /**
     * Create an instance of {@link GetPaymentTypesRequest }
     * 
     */
    public GetPaymentTypesRequest createGetPaymentTypesRequest() {
        return new GetPaymentTypesRequest();
    }

    /**
     * Create an instance of {@link GetPaymentTypesResponse }
     * 
     */
    public GetPaymentTypesResponse createGetPaymentTypesResponse() {
        return new GetPaymentTypesResponse();
    }

    /**
     * Create an instance of {@link PaymentTypes }
     * 
     */
    public PaymentTypes createPaymentTypes() {
        return new PaymentTypes();
    }

    /**
     * Create an instance of {@link TransactionData }
     * 
     */
    public TransactionData createTransactionData() {
        return new TransactionData();
    }

    /**
     * Create an instance of {@link CloseAuthorizationRequest }
     * 
     */
    public CloseAuthorizationRequest createCloseAuthorizationRequest() {
        return new CloseAuthorizationRequest();
    }

    /**
     * Create an instance of {@link CloseAuthorizationResponse }
     * 
     */
    public CloseAuthorizationResponse createCloseAuthorizationResponse() {
        return new CloseAuthorizationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCSServiceFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "TCSServiceFault")
    public JAXBElement<TCSServiceFault> createTCSServiceFault(TCSServiceFault value) {
        return new JAXBElement<TCSServiceFault>(_TCSServiceFault_QNAME, TCSServiceFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartOnlineCollection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "startOnlineCollection")
    public JAXBElement<StartOnlineCollection> createStartOnlineCollection(StartOnlineCollection value) {
        return new JAXBElement<StartOnlineCollection>(_StartOnlineCollection_QNAME, StartOnlineCollection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartOnlineCollectionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "startOnlineCollectionResponse")
    public JAXBElement<StartOnlineCollectionResponseType> createStartOnlineCollectionResponse(StartOnlineCollectionResponseType value) {
        return new JAXBElement<StartOnlineCollectionResponseType>(_StartOnlineCollectionResponse_QNAME, StartOnlineCollectionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompleteOnlineCollection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "completeOnlineCollection")
    public JAXBElement<CompleteOnlineCollection> createCompleteOnlineCollection(CompleteOnlineCollection value) {
        return new JAXBElement<CompleteOnlineCollection>(_CompleteOnlineCollection_QNAME, CompleteOnlineCollection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompleteOnlineCollectionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "completeOnlineCollectionResponse")
    public JAXBElement<CompleteOnlineCollectionResponseType> createCompleteOnlineCollectionResponse(CompleteOnlineCollectionResponseType value) {
        return new JAXBElement<CompleteOnlineCollectionResponseType>(_CompleteOnlineCollectionResponse_QNAME, CompleteOnlineCollectionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompleteOnlineCollectionWithDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "completeOnlineCollectionWithDetails")
    public JAXBElement<CompleteOnlineCollectionWithDetails> createCompleteOnlineCollectionWithDetails(CompleteOnlineCollectionWithDetails value) {
        return new JAXBElement<CompleteOnlineCollectionWithDetails>(_CompleteOnlineCollectionWithDetails_QNAME, CompleteOnlineCollectionWithDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompleteOnlineCollectionWithDetailsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "completeOnlineCollectionWithDetailsResponse")
    public JAXBElement<CompleteOnlineCollectionWithDetailsResponseType> createCompleteOnlineCollectionWithDetailsResponse(CompleteOnlineCollectionWithDetailsResponseType value) {
        return new JAXBElement<CompleteOnlineCollectionWithDetailsResponseType>(_CompleteOnlineCollectionWithDetailsResponse_QNAME, CompleteOnlineCollectionWithDetailsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateForce }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "createForce")
    public JAXBElement<CreateForce> createCreateForce(CreateForce value) {
        return new JAXBElement<CreateForce>(_CreateForce_QNAME, CreateForce.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateForceResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "createForceResponse")
    public JAXBElement<CreateForceResponseType> createCreateForceResponse(CreateForceResponseType value) {
        return new JAXBElement<CreateForceResponseType>(_CreateForceResponse_QNAME, CreateForceResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateForceWithDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "createForceWithDetails")
    public JAXBElement<CreateForceWithDetails> createCreateForceWithDetails(CreateForceWithDetails value) {
        return new JAXBElement<CreateForceWithDetails>(_CreateForceWithDetails_QNAME, CreateForceWithDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateForceWithDetailsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "createForceWithDetailsResponse")
    public JAXBElement<CreateForceWithDetailsResponseType> createCreateForceWithDetailsResponse(CreateForceWithDetailsResponseType value) {
        return new JAXBElement<CreateForceWithDetailsResponseType>(_CreateForceWithDetailsResponse_QNAME, CreateForceWithDetailsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "getDetails")
    public JAXBElement<GetDetails> createGetDetails(GetDetails value) {
        return new JAXBElement<GetDetails>(_GetDetails_QNAME, GetDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "getDetailsResponse")
    public JAXBElement<GetDetailsResponseType> createGetDetailsResponse(GetDetailsResponseType value) {
        return new JAXBElement<GetDetailsResponseType>(_GetDetailsResponse_QNAME, GetDetailsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseAuthorization }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "closeAuthorization")
    public JAXBElement<CloseAuthorization> createCloseAuthorization(CloseAuthorization value) {
        return new JAXBElement<CloseAuthorization>(_CloseAuthorization_QNAME, CloseAuthorization.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CloseAuthorizationResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "closeAuthorizationResponse")
    public JAXBElement<CloseAuthorizationResponseType> createCloseAuthorizationResponse(CloseAuthorizationResponseType value) {
        return new JAXBElement<CloseAuthorizationResponseType>(_CloseAuthorizationResponse_QNAME, CloseAuthorizationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPaymentTypes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "getPaymentTypes")
    public JAXBElement<GetPaymentTypes> createGetPaymentTypes(GetPaymentTypes value) {
        return new JAXBElement<GetPaymentTypes>(_GetPaymentTypes_QNAME, GetPaymentTypes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPaymentTypesResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://fms.treas.gov/services/tcsonline_3_0", name = "getPaymentTypesResponse")
    public JAXBElement<GetPaymentTypesResponseType> createGetPaymentTypesResponse(GetPaymentTypesResponseType value) {
        return new JAXBElement<GetPaymentTypesResponseType>(_GetPaymentTypesResponse_QNAME, GetPaymentTypesResponseType.class, null, value);
    }

}
