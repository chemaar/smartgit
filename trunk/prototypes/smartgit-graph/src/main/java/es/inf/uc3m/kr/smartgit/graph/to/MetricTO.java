package es.inf.uc3m.kr.smartgit.graph.to;

public class MetricTO {
	
	private String title = "";
	private String description = "";
	private String from = "";
	private double value = 0.0;
	
	
	
	public MetricTO(String title, String description, String from, double value) {
		super();
		this.title = title;
		this.description = description;
		this.from = from;
		this.value = value;
	}
	
	
	public MetricTO() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricTO other = (MetricTO) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "MetricTO [title=" + title + ", description=" + description
				+ ", from=" + from + ", value=" + value + "]";
	}
	
	
	
	

}
