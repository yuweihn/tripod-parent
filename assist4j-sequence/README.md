# assist4j-sequence

For example:
-----------------------------------------------------------------------------------------------------------------------------------------------
	create table sequence (
		segment            int(11)                      not null      default 0  comment '分片，从0开始计数',
		name               varchar(255)                 not null,
		current_value      bigint(20)  unsigned         not null,
		create_time        datetime                     not null,
		update_time        datetime                     not null,
	
		primary      key(segment, name)
	) engine=innodb default charset=utf8;
-----------------------------------------------------------------------------------------------------------------------------------------------
	spring:
	  beanSeqNameMap:
	    seqAppKeySecret: seq_app_key_secret
	    seqFeedback: seq_feedback
	    seqFeedbackPic: seq_feedback_pic
	    seqHxAccount: seq_hx_account
	    seqMoment: seq_moment
	    seqMomentComment: seq_moment_comment
	    seqMomentImg: seq_moment_img
	    seqMomentLike: seq_moment_like
	    seqPay: seq_pay
-----------------------------------------------------------------------------------------------------------------------------------------------
	@Bean(name = "sequenceDao", initMethod = "init", destroyMethod = "destroy")
	public SequenceDao sequenceDao(@Qualifier("dataSource") DataSource dataSource
			, @Value("${global.sequence.innerStep:100}") int innerStep
			, @Value("${global.sequence.retryTimes:5}") int retryTimes
			, @Value("${global.sequence.segmentCount:1}") int segmentCount
			, @Value("${global.sequence.maxSkipCount:5}") int maxSkipCount
			, @Value("${global.sequence.maxWaitMillis:5000}") long maxWaitMillis
			, @Value("${global.sequence.ruleClassName:}") String ruleClassName) {
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

	@Bean(name = "fppSequenceBeanHolder")
	@ConfigurationProperties(prefix = "spring", ignoreUnknownFields = true)
	public SequenceBeanHolder fppSequenceBeanHolder() {
		return new SequenceBeanHolder() {
			private Map<String, String> beanSeqNameMap = new HashMap<String, String>();
			
			@Override
			public Map<String, String> getBeanSeqNameMap() {
				return beanSeqNameMap;
			}
		};
	}

	@Bean(name = "sequenceBeanFactory")
	public SequenceBeanFactory sequenceBeanFactory() {
		return new SequenceBeanFactory(DefaultSequence.class, "fppSequenceBeanHolder");
	}
-----------------------------------------------------------------------------------------------------------------------------------------------
	@Resource
	private Sequence seqFeedback;
	@Resource
	private Sequence seqFeedbackPic;
-----------------------------------------------------------------------------------------------------------------------------------------------


