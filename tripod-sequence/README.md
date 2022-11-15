# assist4j-sequence

For example:
------------------------------------------------------------------------------------------------------------------
	create table sequence (
		segment            int(11)                      not null      default 0  comment '分片，从0开始计数',
		name               varchar(255)                 not null,
		current_value      bigint(20)  unsigned         not null,
		create_time        datetime                     not null,
		update_time        datetime                     not null,
	
		primary      key(segment, name)
	) engine=innodb default charset=utf8;
------------------------------------------------------------------------------------------------------------------
	assist4j:
	  sequence:
	    seqAppKeySecret: seq_app_key_secret
	    seqFeedback: seq_feedback
	    seqFeedbackPic: seq_feedback_pic
	    seqHxAccount: seq_hx_account
	    seqMoment: seq_moment
	    seqMomentComment: seq_moment_comment
	    seqMomentImg: seq_moment_img
	    seqMomentLike: seq_moment_like
	    seqPay: seq_pay
------------------------------------------------------------------------------------------------------------------
	@Bean(name = "sequenceDao", initMethod = "init", destroyMethod = "destroy")
	public SequenceDao sequenceDao(@Qualifier("dataSource") DataSource dataSource
			, @Value("${assist4j.sequence-setting.innerStep:100}") int innerStep
			, @Value("${assist4j.sequence-setting.retryTimes:5}") int retryTimes
			, @Value("${assist4j.sequence-setting.segmentCount:1}") int segmentCount
			, @Value("${assist4j.sequence-setting.maxSkipCount:5}") int maxSkipCount
			, @Value("${assist4j.sequence-setting.maxWaitMillis:5000}") long maxWaitMillis
			, @Value("${assist4j.sequence-setting.ruleClassName:}") String ruleClassName) {
		SegmentSequenceDao sequenceDao = new SegmentSequenceDao();
		sequenceDao.setDataSource(dataSource);
		sequenceDao.setInnerStep(innerStep);
		sequenceDao.setRetryTimes(retryTimes);
		sequenceDao.setSegmentCount(segmentCount);
		sequenceDao.setMaxSkipCount(maxSkipCount);
		sequenceDao.setMaxWaitMillis(maxWaitMillis);
		sequenceDao.setRuleClassName(ruleClassName);
		return sequenceDao;
	}

	@Bean(name = "sequenceBeanHolder")
    @ConfigurationProperties(prefix = "assist4j", ignoreUnknownFields = true)
    public SequenceBeanHolder sequenceBeanHolder() {
        return new SequenceBeanHolder() {
            private Map<String, String> sequence = new HashMap<String, String>();
            
            @Override
            public Map<String, String> getSequenceMap() {
                return sequence;
            }
        };
    }

	@Bean(name = "sequenceBeanFactory")
	public SequenceBeanFactory sequenceBeanFactory() {
		return new SequenceBeanFactory(DefaultSequence.class, "sequenceBeanHolder");
	}
------------------------------------------------------------------------------------------------------------------
	@Resource
	private Sequence seqFeedback;
	@Resource
	private Sequence seqFeedbackPic;
	
	
	......
	Feedback feedback = new Feedback();
    feedback.setId(seqFeedback.nextValue());       //fetch next value
    feedback.setUserId(dto.getUserId());
    feedback.setContent(dto.getContent());
    feedback.setContact(dto.getContact());
    feedback.setCreateTime(new Date());
    feedbackDao.save(feedback);
    ......
------------------------------------------------------------------------------------------------------------------


